package hu.beni.clientsupport;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

import java.net.URI;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Client {

	private final RestTemplate restTemplate;

	public <T> ResponseEntity<T> get(URI uri, Class<T> responseType) {
		return restTemplate.exchange(RequestEntity.get(uri).build(), responseType);
	}

	public <T> ResponseEntity<T> get(URI uri, ParameterizedTypeReference<T> responseType) {
		return restTemplate.exchange(RequestEntity.get(uri).build(), responseType);
	}

	public ResponseEntity<Void> post(URI uri) {
		return restTemplate.exchange(RequestEntity.post(uri).build(), Void.class);
	}

	public ResponseEntity<Void> post(URI uri, MultiValueMap<String, String> body) {
		return restTemplate.exchange(RequestEntity.post(uri).contentType(APPLICATION_FORM_URLENCODED).body(body),
				Void.class);
	}

	public <T> ResponseEntity<Void> post(URI uri, T body) {
		return restTemplate.exchange(RequestEntity.post(uri).body(body), Void.class);
	}

	public <T, R> ResponseEntity<R> post(URI uri, T body, Class<R> responseType) {
		return restTemplate.exchange(RequestEntity.post(uri).body(body), responseType);
	}

	public <T, R> ResponseEntity<R> post(URI uri, T body, ParameterizedTypeReference<R> responseType) {
		return restTemplate.exchange(RequestEntity.post(uri).body(body), responseType);
	}

	public ResponseEntity<Void> put(URI uri) {
		return restTemplate.exchange(RequestEntity.put(uri).build(), Void.class);
	}

	public <T, R> ResponseEntity<R> put(URI uri, T body, ParameterizedTypeReference<R> responseType) {
		return restTemplate.exchange(RequestEntity.put(uri).body(body), responseType);
	}

	public <T> ResponseEntity<T> put(URI uri, ParameterizedTypeReference<T> responseType) {
		return restTemplate.exchange(RequestEntity.put(uri).build(), responseType);
	}

	public ResponseEntity<Void> delete(URI uri) {
		return restTemplate.exchange(RequestEntity.delete(uri).build(), Void.class);
	}

	public static URI uri(String url, Object... uriVariables) {
		return UriComponentsBuilder.fromHttpUrl(url).build(uriVariables);
	}
}
