package org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.mappers.complex;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;
import java.util.TreeSet;

import org.aksw.limes.core.io.cache.Cache;
import org.aksw.limes.core.io.mapping.Mapping;
import org.aksw.limes.core.io.mapping.MemoryMapping;
import org.aksw.limes.core.measures.mapper.IMapper.Language;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.AllenAlgebraMapper;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.mappers.atomic.BeginBegin;
import org.aksw.limes.core.measures.mapper.temporal.allenAlgebra.mappers.atomic.EndEnd;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class IsStartedBy extends AllenAlgebraMapper {
    public IsStartedBy() {
	// BB0 \\ (EE0 U EE1)
	this.getRequiredAtomicRelations().add(0);
	this.getRequiredAtomicRelations().add(6);
	this.getRequiredAtomicRelations().add(7);
    }

    @Override
    public String getName() {
	return "IsStartedBy";
    }

    @Override
    public Mapping getMapping(ArrayList<TreeMap<String, Set<String>>> maps) {
	Mapping m = new MemoryMapping();

	TreeMap<String, Set<String>> mapBB0 = maps.get(0);
	TreeMap<String, Set<String>> mapEE0 = maps.get(1);
	TreeMap<String, Set<String>> mapEE1 = maps.get(2);

	for (Map.Entry<String, Set<String>> entryBB0 : mapBB0.entrySet()) {

	    String instanceBB0 = entryBB0.getKey();
	    Set<String> setBB0 = entryBB0.getValue();

	    Set<String> setEE0 = mapEE0.get(instanceBB0);
	    Set<String> setEE1 = mapEE1.get(instanceBB0);

	    if (setEE0 == null)
		setEE0 = new TreeSet<String>();
	    if (setEE1 == null)
		setEE1 = new TreeSet<String>();

	    Set<String> union = AllenAlgebraMapper.union(setEE0, setEE1);
	    Set<String> difference = AllenAlgebraMapper.difference(setBB0, union);

	    if (!difference.isEmpty()) {
		for (String targetInstanceUri : difference) {
		    m.add(instanceBB0, targetInstanceUri, 1);
		}
	    }
	}
	return m;
    }

    @Override
    public Mapping getMapping(Cache source, Cache target, String sourceVar, String targetVar, String expression,
	    double threshold) {
	ArrayList<TreeMap<String, Set<String>>> maps = new ArrayList<TreeMap<String, Set<String>>>();
	EndEnd ee = new EndEnd();
	BeginBegin bb = new BeginBegin();
	// BB0 \\ (EE0 U EE1)
	maps.add(bb.getConcurrentEvents(source, target, expression));
	maps.add(ee.getConcurrentEvents(source, target, expression));
	maps.add(ee.getPredecessorEvents(source, target, expression));
	
	Mapping m = getMapping(maps);
	return m;
    }

    public double getRuntimeApproximation(int sourceSize, int targetSize, double theta, Language language) {
	return 1000d;
    }

    public double getMappingSizeApproximation(int sourceSize, int targetSize, double theta, Language language) {
	return 1000d;
    }

}