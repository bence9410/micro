package hu.beni.amusementpark.helper;

import java.net.URI;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Client {

	private final RestTemplate restTemplate;

	public <T> ResponseEntity<T> get(URI uri, Class<T> responseType){
		return restTemplate.exchange(RequestEntity.get(uri).build(), responseType);
	}
	
	public <T> ResponseEntity<T> get(URI uri, ParameterizedTypeReference<T> responseType) {
		return restTemplate.exchange(RequestEntity.get(uri).build(), responseType);
	}
	
	public <T,R> ResponseEntity<R> post(URI uri, T body, Class<R> responseType) {
		return restTemplate.exchange(RequestEntity.post(uri).body(body), responseType);
	}
	
	public <T,R> ResponseEntity<R> post(URI uri, T body, ParameterizedTypeReference<R> responseType) {
		return restTemplate.exchange(RequestEntity.post(uri).body(body), responseType);
	}
	
	public <T,R> ResponseEntity<R> post(URI uri, MediaType contentType, T body, Class<R> responseType) {
		return restTemplate.exchange(RequestEntity.post(uri).contentType(contentType).body(body), responseType);
	}
	
	public <T,R> ResponseEntity<R> put(URI uri, T body, Class<R> responseType) {
		return restTemplate.exchange(RequestEntity.put(uri).body(body), responseType);
	}
	
	public <T,R> ResponseEntity<R> put(URI uri, T body, ParameterizedTypeReference<R> responseType) {
		return restTemplate.exchange(RequestEntity.put(uri).body(body), responseType);
	}

	public ResponseEntity<Void> delete(URI uri) {
		return restTemplate.exchange(RequestEntity.delete(uri).build(), Void.class);
	}
}
