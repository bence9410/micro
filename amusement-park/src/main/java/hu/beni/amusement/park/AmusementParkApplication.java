package hu.beni.amusement.park;

import java.util.stream.IntStream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import hu.beni.amusement.park.entity.Address;
import hu.beni.amusement.park.entity.AmusementPark;
import hu.beni.amusement.park.entity.Machine;
import hu.beni.amusement.park.enums.MachineType;
import hu.beni.amusement.park.service.AmusementParkService;
import hu.beni.amusement.park.service.MachineService;

@SpringBootApplication
public class AmusementParkApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmusementParkApplication.class, args);
	}
	
	@Bean
	@Profile("!oracleDB")
	public CommandLineRunner createSamleData(AmusementParkService amusementParkService, MachineService machineService) {
		return args -> IntStream.range(0, 10)
				.mapToObj(i -> amusementParkService.save(createAmusementParkWithAddress()).getId())
				.forEach(amusementParkId -> machineService.addMachine(amusementParkId, createMachine()));
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

    public static Machine createMachine() {
        return Machine.builder()
                .fantasyName("Nagy hajó")
                .size(100)
                .price(250)
                .numberOfSeats(10)
                .minimumRequiredAge(18)
                .ticketPrice(10)
                .type(MachineType.CAROUSEL).build();
    }

    public static AmusementPark createAmusementParkWithAddress(){
    	Address address = createAddress();
    	AmusementPark amusementPark = createAmusementPark();
    	address.setAmusementPark(amusementPark);
    	amusementPark.setAddress(address);
    	return amusementPark;
    }
	
}