package hu.beni.tester.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import hu.beni.tester.dto.AmusementParkDTO;
import hu.beni.tester.dto.MachineDTO;
import hu.beni.tester.dto.Page;
import hu.beni.tester.dto.VisitorDTO;
import hu.beni.tester.factory.ValidDTOFactory;
import lombok.RequiredArgsConstructor;

import static hu.beni.tester.constant.Constants.*;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AmusementParkClient {
	
	private static final ParameterizedTypeReference<Resource<AmusementParkDTO>> AMUSEMENT_PARK_DTO_TYPE =
			new ParameterizedTypeReference<Resource<AmusementParkDTO>>() {};
	
	private static final ParameterizedTypeReference<Resource<MachineDTO>> MACHINE_DTO_TYPE =
			new ParameterizedTypeReference<Resource<MachineDTO>>() {};		
	
	private static final ParameterizedTypeReference<Resource<VisitorDTO>> VISITOR_DTO_TYPE =
			new ParameterizedTypeReference<Resource<VisitorDTO>>() {};
			
	private static final ParameterizedTypeReference<Page<Resource<AmusementParkDTO>>> PAGE_OF_AMUSEMENT_PARK_DTO_TYPE =
			new ParameterizedTypeReference<Page<Resource<AmusementParkDTO>>>() {};
			
	private static final ParameterizedTypeReference<List<Resource<MachineDTO>>> LIST_OF_MACHINE_DTO_TYPE =
			new ParameterizedTypeReference<List<Resource<MachineDTO>>>() {};	
					
	private static final ParameterizedTypeReference<Page<Resource<VisitorDTO>>> PAGE_OF_VISITOR_DTO_TYPE = 
			new ParameterizedTypeReference<Page<Resource<VisitorDTO>>>() {};
			
	private final RestTemplate restTemplate;
	
	public HttpHeaders loginAndReturnHeadersWithJSESSIONID(String username, String password) {
    	String cookie = restTemplate.exchange(LOGIN_URL, HttpMethod.POST,
    			createEntityWithFormEncodeing(username, password), String.class)
    			.getHeaders().getFirst(SET_COOKIE);	
    	HttpHeaders httpHeaders = new HttpHeaders();
    	httpHeaders.add(COOKIE, cookie.substring(0, cookie.indexOf(SEMICOLON)));
		return httpHeaders;
	}
	
	public void logout(HttpHeaders headers) {
		restTemplate.exchange(LOGOUT_URL, HttpMethod.POST, createEntityWithHeaders(headers), String.class);
	}
	
	public void deletePark(Long amusementParkId, HttpHeaders headers) {
		restTemplate.exchange(AMUSEMENT_PARK_DELETE_URL, HttpMethod.DELETE, 
				createEntityWithHeaders(headers), String.class, amusementParkId);
	}
	
	public Resource<AmusementParkDTO> postAmusementPark(HttpHeaders headers) {
		return restTemplate.exchange(AMUSEMENT_PARK_URL, HttpMethod.POST, createEntityWithHeaders(
				ValidDTOFactory.createAmusementParkWithAddress(), headers),
				AMUSEMENT_PARK_DTO_TYPE).getBody();
	}
	
	public void postMachine(Long amusementParkId, HttpHeaders headers) {
		restTemplate.exchange(MACHINE_URL, HttpMethod.POST, createEntityWithHeaders(
				ValidDTOFactory.createMachine(), headers), MACHINE_DTO_TYPE, amusementParkId);
	}
	
	public Resource<VisitorDTO> postVisitor(HttpHeaders headers) {
		return restTemplate.exchange(VISITOR_URL, HttpMethod.POST, createEntityWithHeaders(
				ValidDTOFactory.createVisitor(), headers), VISITOR_DTO_TYPE).getBody();
	}
	
	public Page<Resource<AmusementParkDTO>> getAmusementParks(int page, int size, HttpHeaders headers) {
		return restTemplate.exchange(UriComponentsBuilder.fromHttpUrl(PAGED_AMUSEMENT_PARK_URL).queryParam("page", page)
				.queryParam("size", size).build().toUriString(), HttpMethod.GET, createEntityWithHeaders(headers),
				PAGE_OF_AMUSEMENT_PARK_DTO_TYPE).getBody();
	}
	
	public void enterPark(Long amusementParkId, Long visitorId, HttpHeaders headers) {
		restTemplate.exchange(ENTER_PARK_URL, HttpMethod.PUT, createEntityWithHeaders(headers),
				String.class, amusementParkId, visitorId);
	}
	
	public List<Resource<MachineDTO>> getMachineIdsByAmusementParkId(Long amusementParkId, HttpHeaders headers){
		return restTemplate.exchange(MACHINE_URL, HttpMethod.GET, createEntityWithHeaders(headers), 
				LIST_OF_MACHINE_DTO_TYPE, amusementParkId).getBody();
	}
	
	public void getOnMachine(Long amusementParkId, Long machineId, Long visitorId, HttpHeaders headers) {
		restTemplate.exchange(GET_ON_MACHINE_URL, HttpMethod.PUT, createEntityWithHeaders(headers),
				String.class, amusementParkId, machineId, visitorId);
	}
	
	public void getOffMachine(Long amusementParkId, Long machineId, Long visitorId, HttpHeaders headers) {
		restTemplate.exchange(GET_OFF_MACHINE_URL, HttpMethod.PUT, createEntityWithHeaders(headers),
				String.class, amusementParkId, machineId, visitorId);
	}
	
	public void addGuestBookRegistry(Long amusementParkId, Long visitorId, HttpHeaders headers) {
		restTemplate.exchange(ADD_GUEST_BOOK_REGISTRY, HttpMethod.POST, createEntityWithHeaders(
				GUEST_BOOK_REGISTRY_TEXT ,headers), String.class, amusementParkId, visitorId);
	}
	
	public void leavePark(Long amusementParkId, Long visitorId, HttpHeaders headers) {
		restTemplate.exchange(LEAVE_PARK_URL, HttpMethod.PUT, createEntityWithHeaders(headers),
				String.class, amusementParkId, visitorId);
	}
	
	public Page<Resource<VisitorDTO>> getVisitors(int page, int size, HttpHeaders headers) {
		return restTemplate.exchange(UriComponentsBuilder.fromHttpUrl(VISITOR_URL).queryParam("page", page)
				.queryParam("size", size).build().toUriString(), HttpMethod.GET, createEntityWithHeaders(headers),
				PAGE_OF_VISITOR_DTO_TYPE).getBody();
	}
	
	public void deleteVisitor(Long visitorId, HttpHeaders headers) {
		restTemplate.exchange(VISITOR_DELETE_URL, HttpMethod.DELETE, createEntityWithHeaders(headers),
				String.class, visitorId);
	}
	
	private HttpEntity<?> createEntityWithHeaders(HttpHeaders headers) {
		return new HttpEntity<>(headers);
	}
	
	private <T> HttpEntity<T> createEntityWithHeaders(T body, HttpHeaders headers){
		return new HttpEntity<T>(body, headers);
	}
	
	private HttpEntity<MultiValueMap<String, String>> createEntityWithFormEncodeing(String username, String password){
		HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    	MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
    	map.add(USERNAME, username);
    	map.add(PASSWORD, password);
    	
    	return new HttpEntity<MultiValueMap<String, String>>(map, headers);
	}	
}