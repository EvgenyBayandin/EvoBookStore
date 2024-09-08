package ru.fsdstudio.product.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.fsdstudio.product.dto.CustomerDto;

@Service
public class PersonService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    private static final String PERSON_URL = "http://localhost/person/rest/customers/";
    
    public CustomerDto getOne(Long id) {
        try {
            return restTemplate.getForObject(PERSON_URL + id, CustomerDto.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return null;
            } else {
                throw e;
            }
        }
    }
    
    public boolean existsById(Long id) {
        return getOne(id) != null;
    }
}
