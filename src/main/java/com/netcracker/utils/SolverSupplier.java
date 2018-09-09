package com.netcracker.utils;

import com.netcracker.algorithms.AssignmentProblemSolver;
import com.netcracker.algorithms.auction.AuctionAlgorithm;
import com.netcracker.algorithms.auction.implementation.AuctionImplementation;
import com.netcracker.algorithms.auction.implementation.asynchronous.AsynchronousJacobi;
import com.netcracker.algorithms.auction.implementation.synchronous.SynchronousGaussSeidel;
import com.netcracker.algorithms.auction.implementation.synchronous.SynchronousHybrid;
import com.netcracker.algorithms.auction.implementation.synchronous.SynchronousJacobi;
import com.netcracker.algorithms.hungarian.HungarianAlgorithm;

import java.util.LinkedHashMap;
import java.util.Map;

public class SolverSupplier {

    public static Map<String, AssignmentProblemSolver> createSolverMap() {
        Map<String, AssignmentProblemSolver> solverMap = new LinkedHashMap<>();
        solverMap.put("Hungarian", new HungarianAlgorithm());
        createAuctionImplementationMap()
                .forEach(
                        (name, implementation) ->
                                solverMap.put(name, new AuctionAlgorithm(implementation))
                );
        return solverMap;
    }

    public static Map<String, AuctionImplementation> createAuctionImplementationMap() {
        Map<String, AuctionImplementation> map = new LinkedHashMap<>();
//        map.put("SynchronousJacobi", new SynchronousJacobi(4, 4));
//        map.put("SynchronousGaussSeidel", new SynchronousGaussSeidel(4, 4));
//        map.put("SynchronousHybrid", new SynchronousHybrid(4, 2, 2));
        map.put("AsyncronousJacobi", new AsynchronousJacobi(4));
        return map;
    }
}
