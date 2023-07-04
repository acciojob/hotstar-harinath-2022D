package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        Subscription subscription = new Subscription();
        SubscriptionType subscriptionType = subscriptionEntryDto.getSubscriptionType();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());

        Integer totalAmount = 0;
        if(subscriptionType == SubscriptionType.BASIC){
            totalAmount += 500;
            Integer additional = 200 * (subscriptionEntryDto.getNoOfScreensRequired());
            totalAmount += additional;
        } else if (subscriptionType == SubscriptionType.PRO) {
            totalAmount += 800;
            Integer additional = 250 * (subscriptionEntryDto.getNoOfScreensRequired());
            totalAmount += additional;
        }else{
            totalAmount += 1000;
            Integer additional = 350 * (subscriptionEntryDto.getNoOfScreensRequired());
            totalAmount += additional;
        }
        user.setSubscription(subscription);
        subscription.setUser(user);
        userRepository.save(user);
        return totalAmount;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user=userRepository.findById(userId).get();
        if(user.getSubscription().getSubscriptionType().toString().equals("ELITE")){
            throw new Exception("Already the best Subscription");
        }

        Subscription subscription=user.getSubscription();
        Integer previousFair=subscription.getTotalAmountPaid();
        Integer currentFair;
        if(subscription.getSubscriptionType().equals(SubscriptionType.BASIC)){
            subscription.setSubscriptionType(SubscriptionType.PRO);
            currentFair =previousFair+300+(50*subscription.getNoOfScreensSubscribed());
        }else {
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            currentFair=previousFair+200+(100*subscription.getNoOfScreensSubscribed());
        }

        subscription.setTotalAmountPaid(currentFair);
        user.setSubscription(subscription);
        subscriptionRepository.save(subscription);

        return currentFair-previousFair;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        List<Subscription> subscriptions = subscriptionRepository.findAll();
        Integer revenue = 0;
        for(Subscription subscription : subscriptions){
            int amt = subscription.getTotalAmountPaid();
            revenue += amt;
        }
        return revenue;
    }

}
