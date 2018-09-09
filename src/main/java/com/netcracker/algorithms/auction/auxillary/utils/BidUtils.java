package com.netcracker.algorithms.auction.auxillary.utils;

import com.netcracker.algorithms.auction.auxillary.entities.aggregates.BenefitMatrix;
import com.netcracker.algorithms.auction.auxillary.entities.aggregates.ItemList;
import com.netcracker.algorithms.auction.auxillary.entities.aggregates.PriceVector;
import com.netcracker.algorithms.auction.auxillary.entities.basic.Item;
import com.netcracker.algorithms.auction.auxillary.entities.basic.Person;
import com.netcracker.algorithms.auction.auxillary.entities.basic.SearchTaskResult;

public class BidUtils {

    public static SearchTaskResult findBestItem(Person person,
                                                ItemList itemList,
                                                PriceVector priceVector,
                                                BenefitMatrix benefitMatrix) {
        Item currentBestItem = null;
        double currentBestValue = -1.0;
        double currentSecondBestValue = -1.0;
        for (Item item : itemList) {
            int benefit = benefitMatrix.getBenefit(person, item);
            double price = priceVector.getPriceFor(item);
            double value = benefit - price;
            if (value > currentBestValue) {
                currentBestItem = item;
                currentBestValue = value;
                currentSecondBestValue = currentBestValue;
            } else if (value > currentSecondBestValue) {
                currentSecondBestValue = value;
            }
        }
        return new SearchTaskResult(
                currentBestItem,
                currentBestValue,
                currentSecondBestValue
        );
    }
}
