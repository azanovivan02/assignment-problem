package com.netcracker.algorithms.auction.implementation.synchronous;

import com.netcracker.algorithms.auction.auxillary.entities.aggregates.BenefitMatrix;
import com.netcracker.algorithms.auction.auxillary.entities.aggregates.ItemList;
import com.netcracker.algorithms.auction.auxillary.entities.aggregates.PersonQueue;
import com.netcracker.algorithms.auction.auxillary.entities.aggregates.PriceVector;
import com.netcracker.algorithms.auction.auxillary.entities.basic.Bid;
import com.netcracker.algorithms.auction.auxillary.entities.basic.Person;
import com.netcracker.algorithms.auction.auxillary.entities.tasks.ParallelBidTask;
import com.netcracker.algorithms.auction.implementation.AuctionImplementation;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static com.netcracker.algorithms.auction.auxillary.utils.ConcurrentUtils.executeCallableList;
import static com.netcracker.utils.GeneralUtils.flatten;

/**
 * Hybrid synchronous implementation.
 * It creates several separate bid tasks, inside which several search tasks are created for each person.
 * So we achieve parallel execution both across people and within each bid.
 */
public class SynchronousHybrid extends AbstractSynchronousAuctionImplementation implements AuctionImplementation {

    private final int numberOfPersonsPerBidTasks;
    private final int numberOfSearchTasksPerPerson;

    /**
     * @param numberOfThreads
     * @param numberOfPersonsPerBidTasks
     * @param numberOfSearchTasksPerPerson
     */
    public SynchronousHybrid(int numberOfThreads,
                             int numberOfPersonsPerBidTasks,
                             int numberOfSearchTasksPerPerson) {
        super(numberOfThreads);
        this.numberOfPersonsPerBidTasks = numberOfPersonsPerBidTasks;
        this.numberOfSearchTasksPerPerson = numberOfSearchTasksPerPerson;
    }

    public int getNumberOfPersonsPerBidTasks() {
        return numberOfPersonsPerBidTasks;
    }

    public int getNumberOfSearchTasksPerPerson() {
        return numberOfSearchTasksPerPerson;
    }

    @Override
    public List<Bid> makeBids(BenefitMatrix benefitMatrix,
                              PriceVector priceVector,
                              double epsilon,
                              PersonQueue nonAssignedPersonQueue,
                              ItemList itemList,
                              ExecutorService executorService) {
        final List<ParallelBidTask> bidTaskList = new LinkedList<>();
        while (!nonAssignedPersonQueue.isEmpty()) {
            List<Person> personList = nonAssignedPersonQueue.removeSeveral(numberOfPersonsPerBidTasks);
            ParallelBidTask bidTask = new ParallelBidTask(
                    benefitMatrix,
                    priceVector,
                    personList,
                    itemList,
                    epsilon,
                    executorService,
                    numberOfSearchTasksPerPerson
            );
            bidTaskList.add(bidTask);
        }
        return flatten(executeCallableList(bidTaskList, executorService));
    }
}
