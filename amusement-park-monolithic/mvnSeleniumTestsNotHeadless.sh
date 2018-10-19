mvn surefire:test -Dtest=SeleniumTests -DargLine="-Dselenium.only.test.to.run=${1} -Dselenium.headless=false" -DtestFailureIgnore=false
