var amusementPark = {
	name : "Beni parkja",
	capital : 3000,
	totalArea : 1000,
	entranceFee : 50
}

var address = {
	country : "Magyarország",
	zipCode : "1148",
	city : "Budapest",
	street : "Fogarasi út",
	houseNumber : "80/C"
}

driver.write("#loginUsername", "admin");
driver.write("#password", "password");
driver.click("#login");

driver.text("#username", "admin");

driver.write("#parkName", amusementPark.name);
driver.write("#capital", amusementPark.capital);
driver.write("#totalArea", amusementPark.totalArea);
driver.write("#entranceFee", amusementPark.entranceFee);

driver.write("#country", address.country);
driver.write("#zipCode", address.zipCode);
driver.write("#city", address.city);
driver.write("#street", address.street);
driver.write("#houseNumber", address.houseNumber);

driver.click("#save");

driver.text("#result", "success");

driver.text("#tableBody>tr>td:nth-child(1)", amusementPark.name);
driver.text("#tableBody>tr>td:nth-child(2)", amusementPark.capital);
driver.text("#tableBody>tr>td:nth-child(3)", amusementPark.totalArea);
driver.text("#tableBody>tr>td:nth-child(4)", amusementPark.entranceFee);
driver.text("#tableBody>tr>td:nth-child(5)", address.country + " " + address.zipCode + " "
		+ address.city + " " + address.street + " " + address.houseNumber);
driver.visible("#tableBody>tr>td:nth-child(6)>input[value='Delete']");