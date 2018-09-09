package com.netcracker.algorithms.auction.auxillary.entities.tasks;

import com.netcracker.algorithms.auction.auxillary.entities.aggregates.BenefitMatrix;
import com.netcracker.algorithms.auction.auxillary.entities.aggregates.ItemList;
import com.netcracker.algorithms.auction.auxillary.entities.aggregates.PriceVector;
import com.netcracker.algorithms.auction.auxillary.entities.basic.Person;
import com.netcracker.algorithms.auction.auxillary.entities.basic.SearchTaskResult;
import com.netcracker.algorithms.auction.auxillary.utils.BidUtils;

import java.util.concurrent.Callable;

import static com.netcracker.algorithms.auction.auxillary.utils.BidUtils.findBestItem;

public class SearchTask implements Callable<SearchTaskResult> {

    private final BenefitMatrix benefitMatrix;
    private final PriceVector priceVector;
    private final Person person;
    private final ItemList itemList;

    public SearchTask(BenefitMatrix benefitMatrix,
                      PriceVector priceVector,
                      Person person,
                      ItemList itemList) {
        this.benefitMatrix = benefitMatrix;
        this.priceVector = priceVector;
        this.person = person;
        this.itemList = itemList;
    }

    @Override
    public SearchTaskResult call() {
        return findBestItem(person, itemList, priceVector, benefitMatrix);
    }

    public BenefitMatrix getBenefitMatrix() {
        return benefitMatrix;
    }

    public PriceVector getPriceVector() {
        return priceVector;
    }

    public Person getPerson() {
        return person;
    }

    public ItemList getItemList() {
        return itemList;
    }

    @Override
    public String toString() {
        return "SearchTask{" +
                "person=" + person +
                ", itemList=" + itemList +
                '}';
    }
}
