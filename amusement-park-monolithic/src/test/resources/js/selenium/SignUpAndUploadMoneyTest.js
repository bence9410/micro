driver.click("#goToSignUp");

driver.write("#signUpUsername", "visitor");
driver.write("#signUpPassword", "password");
driver.write("#signUpConfirmPassword", "password");
driver.write("#dateOfBirth", "1994-10-10");
driver.click("#signUp");

driver.text("#username", "visitor");
driver.text("#spendingMoney", "250");

driver.click("#uploadMoney");
driver.write("#money", "100");
driver.click("#upload");
driver.text("#moneyUploadResult", "success");
driver.click("#closeUpload");
driver.hidden("#money");

driver.text("#spendingMoney", "350");