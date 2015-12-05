package com.enigmastation.ml.perceptron.impl;

import com.enigmastation.ml.model.Layer;
import com.enigmastation.ml.model.Neuron;
import com.enigmastation.ml.model.Synapse;
import com.enigmastation.ml.perceptron.Perceptron;
import com.enigmastation.ml.util.SessionManager;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.primitives.Longs;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;

import java.util.*;

class Network {
    long[] wordids = {};
    long[] hiddenids = {};
    long[] urlids = {};
    Map<Long, Double> ai = Maps.newHashMap();
    Map<Long, Double> ah = Maps.newHashMap();
    Map<Long, Double> ao = Maps.newHashMap();
    Table<Long, Long, Double> wi = HashBasedTable.create();
    Table<Long, Long, Double> wo = HashBasedTable.create();
}

public class PerceptronImpl implements Perceptron {
    public PerceptronImpl() {

    }

    protected Network createNetwork(long[] from, long[] to) {
        Session session = null;
        Transaction tx = null;
        try {
            session = SessionManager.getSession();
            tx = session.beginTransaction();
            return createNetwork(from, to, session, tx);
        } finally {
            if (tx != null && tx.getStatus().equals(TransactionStatus.ACTIVE)) {
                tx.commit();
            }
            if (session != null) {
                session.close();
            }
        }
    }

    private Network createNetwork(long[] from, long[] to, Session session, Transaction tx) {
        long[] sortedFrom = sortedUniqueArray(from);
        long[] sortedTo = sortedUniqueArray(to);
        Network network = new Network();

        network.wordids = sortedFrom;
        network.hiddenids = getAllHiddenIds(sortedFrom, sortedTo, session, tx);
        network.urlids = sortedTo;

        fillMapWithStrength(network.ai, network.wordids, 1.0);
        fillMapWithStrength(network.ah, network.hiddenids, 1.0);
        fillMapWithStrength(network.ao, network.urlids, 1.0);

        for (long w : network.wordids) {
            for (long h : network.hiddenids) {
                network.wi.put(w, h, getStrength(w, h, Layer.HIDDEN, session, tx));
            }
        }
        for (long h : network.hiddenids) {
            for (long o : network.urlids) {
                network.wo.put(h, o, getStrength(h, o, Layer.OUTPUT, session, tx));
            }
        }
        return network;
    }

    protected long[] sortedUniqueArray(long[] from) {
        Set<Long> longs = Sets.newTreeSet();
        Arrays.stream(from).forEach(longs::add);
        return Longs.toArray(longs);
    }

    private void fillMapWithStrength(Map<Long, Double> map, long[] ids, double v) {
        for (long i : ids) {
            map.put(i, v);
        }
    }

    protected Map<Long, Double> feedforward(Network network) {
        for (long i : network.wordids) {
            network.ai.put(i, 1.0);
        }
        for (long j : network.hiddenids) {
            double sum = 0.0;
            for (long i : network.wordids) {
                sum += network.ai.get(i) * network.wi.get(i, j);
            }
            network.ah.put(j, Math.tanh(sum));
        }
        for (long k : network.urlids) {
            double sum = 0.0;
            for (long j : network.hiddenids) {
                sum += network.ah.get(j) * network.wo.get(j, k);
            }
            network.ao.put(k, Math.tanh(sum));
        }
        return network.ao;
    }

    @Override
    public Map<Long, Double> getResults(long[] wordids, long[] urlids) {
        Network network = createNetwork(wordids, urlids);
        return feedforward(network);
    }

    protected void backPropagate(Network network, Map<Long, Double> targets) {
        backPropagate(network, targets, 0.5);
    }

    protected void backPropagate(Network network, Map<Long, Double> targets, double N) {
        Map<Long, Double> output_deltas = Maps.newHashMap();
        Map<Long, Double> hidden_deltas = Maps.newHashMap();
        for (long k : network.urlids) {
            double error = targets.get(k) - network.ao.get(k);
            output_deltas.put(k, error * dtanh(network.ao.get(k)));
        }
        for (long j : network.hiddenids) {
            double error = 0.0;
            for (long k : network.urlids) {
                error += output_deltas.get(k) * network.wo.get(j, k);
            }
            hidden_deltas.put(j, error * dtanh(network.ah.get(j)));
        }
        for (long j : network.hiddenids) {
            for (long k : network.urlids) {
                double change = output_deltas.get(k) * network.ah.get(j);
                network.wo.put(j, k, network.wo.get(j, k) + N * change);
            }
        }
        for (long i : network.wordids) {
            for (long j : network.hiddenids) {
                double change = hidden_deltas.get(j) * network.ai.get(i);
                network.wi.put(i, j, network.wi.get(i, j) + N * change);
            }
        }
    }

    @Override
    public void trainquery(long[] inputs, long[] outputs, long target) {
        validateTrainingData(outputs, target);
        Session session = null;
        Transaction tx = null;
        try {
            session = SessionManager.getSession();
            tx = session.beginTransaction();
            trainquery(inputs, outputs, target, session, tx);
        } finally {
            if (tx != null && tx.getStatus().equals(TransactionStatus.ACTIVE)) {
                tx.commit();
            }
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public long getNeuronIdFor(String key) {
        Session session = null;
        Transaction tx = null;
        try {
            session = SessionManager.getSession();
            tx = session.beginTransaction();
            return getNeuronIdFor(key, session, tx);
        } finally {
            if (tx != null && tx.getStatus().equals(TransactionStatus.ACTIVE)) {
                tx.commit();
            }
            if (session != null) {
                session.close();
            }
        }
    }

    private long getNeuronIdFor(String key, Session session, Transaction tx) {
        Long id = getNeuronId(key, session, tx);
        if (id == null) {
            Neuron neuron = new Neuron(key);
            session.save(neuron);
            id = neuron.getId();
        }
        return id;
    }

    private void validateTrainingData(long[] outputs, long target) {
        if (Arrays.stream(outputs).filter(l -> l == target).count() != 1) {
            throw new RuntimeException("Training targets do not include actual target");
        }
    }

    protected void trainquery(long[] inputs, long[] outputs, long target, Session session, Transaction tx) {
        validateTrainingData(outputs, target);
        generateHiddenNode(inputs, outputs, session, tx);
        Network network = createNetwork(inputs, outputs, session, tx);
        feedforward(network);
        Map<Long, Double> targets = new HashMap<>();
        fillMapWithStrength(targets, outputs, 0.0);
        targets.put(target, 1.0);
        backPropagate(network, targets);
        updateDatabase(network, session, tx);
    }

    private void updateDatabase(Network network, Session session, Transaction tx) {
        for (long i : network.wordids) {
            for (long j : network.hiddenids) {
                setStrength(i, j, Layer.HIDDEN, network.wi.get(i, j), session, tx);
            }
        }
        for (long j : network.hiddenids) {
            for (long k : network.urlids) {
                setStrength(j, k, Layer.OUTPUT, network.wo.get(j, k), session, tx);
            }
        }
    }

    protected void generateHiddenNode(long[] from, long[] to) {
        Session session = null;
        Transaction tx = null;
        try {
            session = SessionManager.getSession();
            tx = session.beginTransaction();
            generateHiddenNode(from, to, session, tx);
        } finally {
            if (tx != null && tx.getStatus().equals(TransactionStatus.ACTIVE)) {
                tx.commit();
            }
            if (session != null) {
                session.close();
            }
        }
    }

    protected long[] getAllHiddenIds(long[] from, long[] to) {
        Session session = null;
        Transaction tx = null;
        try {
            session = SessionManager.getSession();
            tx = session.beginTransaction();

            return getAllHiddenIds(from, to, session, tx);
        } finally {
            if (tx != null && tx.getStatus().equals(TransactionStatus.ACTIVE)) {
                tx.commit();
            }
            if (session != null) {
                session.close();
            }
        }

    }

    private long[] getAllHiddenIds(long[] from, long[] to, Session session, Transaction tx) {
        Set<Long> ids = Sets.newTreeSet();

        long[] sortedFrom = sortedUniqueArray(from);
        long[] sortedTo = sortedUniqueArray(to);

        Query query = session.createQuery("from Synapse s");
        @SuppressWarnings("unchecked")
        List<Synapse> synapses = (List<Synapse>) query.list();
        for (Synapse s : synapses) {
            switch (s.getLayer()) {
                case HIDDEN:
                    if (Arrays.binarySearch(sortedFrom, s.getInput()) > -1) {
                        ids.add(s.getOutput());
                    }
                    break;
                case OUTPUT:
                    if (Arrays.binarySearch(sortedTo, s.getOutput()) > -1) {
                        ids.add(s.getInput());
                    }
                    break;
            }
        }
        return Longs.toArray(ids);
    }

    private void generateHiddenNode(long[] from, long[] to, Session session, Transaction tx) {
        long[] sortedFrom = Arrays.copyOf(from, from.length);
        Arrays.sort(sortedFrom);
        StringJoiner joiner = new StringJoiner(":", "<", ">");
        for (long f : sortedFrom) {
            joiner.add(Long.toString(f));
        }
        String key = joiner.toString();
        Long id = getNeuronId(key, session, tx);
        if (id == null) {
            Neuron neuron = new Neuron(key);
            session.save(neuron);
            for (long f : from) {
                setStrength(f, neuron.getId(), Layer.HIDDEN, 1.0 / from.length);
            }
            for (long t : to) {
                setStrength(neuron.getId(), t, Layer.OUTPUT, 0.1);
            }
        }
    }

    protected double getStrength(long from, long to, Layer layer) {
        Session session = null;
        Transaction tx = null;
        try {
            session = SessionManager.getSession();
            tx = session.beginTransaction();
            return getStrength(from, to, layer, session, tx);
        } finally {
            if (tx != null && tx.getStatus().equals(TransactionStatus.ACTIVE)) {
                tx.commit();
            }
            if (session != null) {
                session.close();
            }
        }
    }

    protected void setStrength(long from, long to, Layer layer, double value) {
        Session session = null;
        Transaction tx = null;
        try {
            session = SessionManager.getSession();
            tx = session.beginTransaction();
            setStrength(from, to, layer, value, session, tx);
        } finally {
            if (tx != null && tx.getStatus().equals(TransactionStatus.ACTIVE)) {
                tx.commit();
            }
            if (session != null) {
                session.close();
            }
        }
    }

    private long getNeuronId(String key) {
        Session session = null;
        Transaction tx = null;
        try {
            session = SessionManager.getSession();
            tx = session.beginTransaction();
            return getNeuronId(key, session, tx);
        } finally {
            if (tx != null && tx.getStatus().equals(TransactionStatus.ACTIVE)) {
                tx.commit();
            }
            if (session != null) {
                session.close();
            }
        }
    }

    private Long getNeuronId(String key, Session session, Transaction tx) {
        Query query = session.getNamedQuery("neuron.getNeuronId");
        query.setParameter("key", key);
        return (Long) query.uniqueResult();
    }

    private void populateQuery(Query query, long from, long to, Layer layer) {
        query.setParameter("input", from);
        query.setParameter("output", to);
        query.setParameter("layer", layer);
    }

    private void setStrength(long from, long to, Layer layer, double value, Session session, Transaction tx) {
        Query query = session.getNamedQuery("synapse.findSynapse");
        populateQuery(query, from, to, layer);
        Synapse s = (Synapse) query.uniqueResult();
        if (s == null) {
            s = new Synapse(from, to, layer, value);
            session.save(s);
        } else {
            s.setStrength(value);
        }
    }

    private double getStrength(long from, long to, Layer layer, Session session, Transaction tx) {
        Query query = session.getNamedQuery("synapse.getStrength");
        populateQuery(query, from, to, layer);
        Double o = (Double) query.uniqueResult();
        if (o == null) {
            return layer.getStrength();
        } else {
            return o;
        }
    }
}
