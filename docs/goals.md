Övergripande mål, satt av Wulff: Utvärdera Kafka som eventbus



Begränsningar/krav (mina):

"LeksaksAPI" i back/front för att slippa tidsspillan på access i teliasystem
Vi bygger plattformen stegvis, först med den inbyggda eventbussen så vi kan testa samspelet mellan frontend/eventbus/backend, därefter med en mycket enkel eventbus i Redis, som vi slutligen byter mot att köra Kafka.



Om det finns tid när vi är framme vid Kafka så kommer vi testa lite avancerade features i plattformen och prestanda.



Enkel arkitektur:



Klient<---JSON--->[FrontendApp<---eventbuss--->BackendApp]<---JSON--->Backend



I steg 1 är det inom [...] en och samma applikation, i steg 2 och 3 så är det separata applikationer



Krav:

Skall skrivas för Quarkus och använda plattformens extensions så långt som möjligt, gärna reaktivt.
Kod versionshanteras i Github där vi också hanterar issues och dokumentation.
Inget krav på grafiskt GUI
Jag kommer styra upp arbetet så vi använder rätt och moderna extensions.
