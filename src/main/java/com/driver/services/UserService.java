package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        user = userRepository.save(user);
        return user.getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository

        Integer cnt = 0;

        User user = userRepository.findById(userId).get();
        Subscription subscription = user.getSubscription();
        int age = user.getAge();

        List<WebSeries> webSeries = webSeriesRepository.findAll();
        for(WebSeries webSeries1 : webSeries){
            int ageLimit = webSeries1.getAgeLimit();
            if(age >= ageLimit){
                if(subscription.getSubscriptionType() == webSeries1.getSubscriptionType()){
                    cnt++;
                }
            }
        }
        return cnt;
    }


}
