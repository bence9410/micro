package hu.beni.amusementpark;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import hu.beni.amusementpark.entity.Address;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.repository.AmusementParkRepository;

@SpringBootApplication
public class AmusementParkApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmusementParkApplication.class, args);
	}

	@Bean
	CommandLineRunner init(AmusementParkRepository amusementParkRepository) {
		return args -> amusementParkRepository.save(IntStream.range(0, 4)
				.mapToObj(i -> createAmusementParkWithAddress()).collect(Collectors.toList()));
	}

	public static AmusementPark createAmusementParkWithAddress() {
		Address address = createAddress();
		AmusementPark amusementPark = createAmusementPark();
		address.setAmusementPark(amusementPark);
		amusementPark.setAddress(address);
		return amusementPark;
	}
	
	public static Address createAddress() {
        return Address.builder()
                .zipCode("1148")
                .city("Budapest")
                .country("Magyarország")
                .street("Fogarasi út")
                .houseNumber("80/C").build();
    }
	
	public static AmusementPark createAmusementPark() {
        return AmusementPark.builder()
                .name("Beni parkja")
                .capital(3000)
                .totalArea(1000)
                .entranceFee(50).build();
    }

}