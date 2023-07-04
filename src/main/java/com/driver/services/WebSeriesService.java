package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo
        WebSeries webSeries = new WebSeries();
        String name = webSeriesEntryDto.getSeriesName();

        if(webSeriesRepository.findBySeriesName(name) != null) throw new Exception("Series is already present");

        webSeries.setSeriesName(name);
        webSeries.setAgeLimit(webSeriesEntryDto.getAgeLimit());
        webSeries.setSubscriptionType(webSeriesEntryDto.getSubscriptionType());
        webSeries.setRating(webSeriesEntryDto.getRating());
        ProductionHouse productionHouse = productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId()).get();
        webSeries.setProductionHouse(productionHouse);

        webSeries = webSeriesRepository.save(webSeries);
        double productionrating = productionHouse.getRatings();
        List<WebSeries> webSeries1 = productionHouse.getWebSeriesList();

        double totalRatings = productionrating * webSeries1.size();

        totalRatings += webSeriesEntryDto.getRating();
        webSeries1.add(webSeries);

        productionHouse.setWebSeriesList(webSeries1);
        productionHouse.setRatings(totalRatings/webSeries1.size());

        productionHouseRepository.save(productionHouse);
        webSeriesRepository.save(webSeries);

        return webSeries.getId();
    }

}
