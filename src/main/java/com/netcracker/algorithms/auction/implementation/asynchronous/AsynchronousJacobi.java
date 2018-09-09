package com.netcracker.algorithms.auction.implementation.asynchronous;

import com.netcracker.algorithms.auction.auxillary.entities.aggregates.*;
import com.netcracker.algorithms.auction.implementation.AuctionImplementation;
import com.netcracker.utils.io.AssertionUtils;
import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;

import static com.netcracker.algorithms.auction.auxillary.entities.aggregates.Assignment.createInitialAssignment;
import static com.netcracker.algorithms.auction.auxillary.entities.aggregates.ItemList.createFullItemList;
import static com.netcracker.algorithms.auction.auxillary.entities.aggregates.PersonQueue.createFullPersonQueue;
import static com.netcracker.utils.ConcurrentUtils.*;
import static com.netcracker.utils.ConcurrentUtils.createExecutorService;
import static com.netcracker.utils.io.AssertionUtils.customAssert;
import static com.netcracker.utils.io.logging.StaticLoggerHolder.info;

public class AsynchronousJacobi implements AuctionImplementation {

    private final int numberOfThreads;

    public AsynchronousJacobi(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    @Override
    public Assignment epsilonScalingPhase(BenefitMatrix benefitMatrix,
                                          PriceVector priceVector,
                                          double epsilon) {

        info("Begin phase with e = %.2f", epsilon);

        final ExecutorService executorService = createExecutorService(numberOfThreads);

        final int n = benefitMatrix.size();

        CyclicBarrier startBarrier = getStartBarrier(numberOfThreads);
        CyclicBarrier endBarrier = getEndBarrier(numberOfThreads);

        final PersonQueue nonAssignedPersonQueue = createFullPersonQueue(n);
        final ItemList itemList = createFullItemList(n);
        final Assignment assignment = createInitialAssignment(n);

        List<Runnable> runnableList = new ArrayList<>();
        for (int threadId = 0; threadId < numberOfThreads; threadId++) {
            ActingThread thread = new ActingThread(
                    threadId,
                    benefitMatrix,
                    priceVector,
                    itemList,
                    assignment,
                    nonAssignedPersonQueue,
                    epsilon,
                    startBarrier,
                    endBarrier
            );
            runnableList.add(thread);
        }

        executeRunnableList(runnableList, executorService);

        executorService.shutdown();

        info("End phase with e = %.2f", epsilon);

        info("Assignment: %s", assignment.toString());

        customAssert(assignment.isComplete(), "Assignment is not complete");

        return assignment;
    }

    private static CyclicBarrier getStartBarrier(int numberOfThreads) {
        Runnable action = () -> {
            info("All threads reached start barrier");
        };
        return new CyclicBarrier(numberOfThreads, action);
    }

    private static CyclicBarrier getEndBarrier(int numberOfThreads) {
        Runnable action = () -> {
            info("All threads reached start barrier");
        };
        return new CyclicBarrier(numberOfThreads, action);
    }

    public static void main(String[] args) {
        AsynchronousJacobi asynchronousJacobi = new AsynchronousJacobi(4);
        BenefitMatrix benefitMatrix = new BenefitMatrix(new int[1][1]);
        PriceVector priceVector = PriceVector.createInitialPriceVector(4);

        asynchronousJacobi.epsilonScalingPhase(benefitMatrix, priceVector, 0.0);
    }
}
