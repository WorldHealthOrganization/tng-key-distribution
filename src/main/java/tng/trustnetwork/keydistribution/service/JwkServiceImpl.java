package tng.trustnetwork.keydistribution.service;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
public class JwkServiceImpl implements JwkService {
    private String urldata = "http://localhost:8080/trustlist";
    //private String getApiKey = "";
    //private String getApiPwd = "";
    
    //actual gateway API to be accessed: !!!! private String urldata = "https://tng-dev.who.int/trustList";
    
    RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<String> gatewayApiCall() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            HttpEntity<String> httpEntity  = new HttpEntity<String>("parameters", headers);
            ResponseEntity<String> response = restTemplate.exchange(urldata, HttpMethod.GET, 
                httpEntity, String.class);
            return response;
        }   catch (Exception e) {
            log.error("error when reading", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "exception calling API endpoint",e);
        }
    }
    
    //    public List<TrustListItemDto> gatewayApiCall() {
    //        try {
    //            HttpHeaders headers = new HttpHeaders();
    //            headers.set("Key", getApiKey);
    //            headers.set("Pwd", getApiPwd);
    //            ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, 
    //               new HttpEntity<>(headers), List.class);
    //            log.info("output is ", response.getBody());    
    //            return response.getBody();
    //        }   catch (Exception e) {
    //            log.error("error when reading", e);
    //            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
    //               "exception calling API endpoint",e);
    //        }
    //    }
}



