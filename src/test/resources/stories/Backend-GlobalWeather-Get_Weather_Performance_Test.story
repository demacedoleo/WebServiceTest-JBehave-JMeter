[Automated Test : US-35] Verify Global Weather Temperature

Meta:
@team charlie alpha
@category globalweather
@storyId GW-35
@suite Aceptance
@story backend

Narrative:
In order to Know weather report for all cities around the world
As a Client WebServices
I want to Check Weather By Country and City Name

Scenario: Check weather by country and city name
Given A country Brazil and city Rio de Janeiro
When I consume GetWeather service
Then Verify that temperature is 40 C



