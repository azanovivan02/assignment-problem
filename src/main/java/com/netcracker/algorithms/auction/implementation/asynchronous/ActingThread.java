package com.netcracker.algorithms.auction.implementation.asynchronous;

import com.netcracker.algorithms.auction.auxillary.entities.aggregates.*;
import com.netcracker.algorithms.auction.auxillary.entities.basic.Item;
import com.netcracker.algorithms.auction.auxillary.entities.basic.Person;
import com.netcracker.algorithms.auction.auxillary.entities.basic.SearchTaskResult;

import java.util.concurrent.CyclicBarrier;

import static com.netcracker.algorithms.auction.auxillary.utils.BidUtils.findBestItem;
import static com.netcracker.utils.ConcurrentUtils.await;
import static com.netcracker.utils.io.logging.StaticLoggerHolder.info;

//todo find better name
class ActingThread implements Runnable {

    private final int threadId;
    private final BenefitMatrix benefitMatrix;
    private final PriceVector priceVector;
    private final ItemList itemList;
    private final Assignment assignment;
    private final PersonQueue personQueue;
    private final double epsilon;

    private final CyclicBarrier startBarrier;
    private final CyclicBarrier endBarrier;

    public ActingThread(int threadId,
                        BenefitMatrix benefitMatrix,
                        PriceVector priceVector,
                        ItemList itemList,
                        Assignment assignment,
                        PersonQueue personQueue,
                        double epsilon,
                        CyclicBarrier startBarrier,
                        CyclicBarrier endBarrier) {
        this.threadId = threadId;
        this.benefitMatrix = benefitMatrix;
        this.priceVector = priceVector;
        this.itemList = itemList;
        this.assignment = assignment;
        this.personQueue = personQueue;
        this.epsilon = epsilon;
        this.startBarrier = startBarrier;
        this.endBarrier = endBarrier;
    }

    @Override
    public void run() {
        await(startBarrier);

        info("[%d] Thread started e-scaling phase", threadId);

        while (!personQueue.isEmpty()) {
            Person person;
            synchronized (personQueue) {
                person = personQueue.remove();
            }
            if (person == null) {
                break;
            }

            SearchTaskResult result = findBestItem(
                    person,
                    itemList,
                    priceVector,
                    benefitMatrix
            );

            final double bidValue = result.getBidValue(epsilon);
            final Item bestItem = result.getBestItem();
            synchronized (bestItem) {
                priceVector.increasePrice(bestItem, bidValue);
                assignment.setPersonForItem(bestItem, person);
            }
        }

        info("[%d] Thread finished e-scaling phase", threadId);

        await(endBarrier);
    }
}
