#SemStat for SEO and SEM

![semstat](https://cloud.githubusercontent.com/assets/12084504/18876316/70449052-84c9-11e6-8359-4ddae7fbb030.jpg)

## Description
This robot get unique domain with website content keyword. Interface is for English and Polish languages.

## Polish description
SemStat to oprogramowanie służące do poszukiwania domen internetowych, zawierających przynajmniej jedną
wybraną frazę. Robot pobiera stronę internetową, sprawdza czy zawiera ona frazę i jeżeli zawiera wybraną frazę
to domena tej strony dodawana jest razem ze słowem kluczowym jako niepowtarzalna pozycja.

Przykładowo robot wchodzi na stronę http://oswietlenie.dev/oswietlenia-lampy.html i jeżeli znajdzie
na niej frazę "oświetlenia" to do bazy danych dodawana jest domena "oswietlenie.dev" wraz ze słowem kluczowym.
Program uniemożliwia zdublowanie dwóch pozycji w bazie danych o jednakowej domenie i frazie.

Robot musi od czegoś zacząć pracę, aby zebrać wyjściowe strony internetowe dla frazy. Do dyspozycji jest
zintegrowane Google API Search Custom. Oprócz Google API Search Custom, program oferuje opcję
ręcznego wprowadzenia adresów wyjściowych stron internetowych. Przykładowo jako wyjściową stronę
można dodać http://wyszukiwarka.dev/query=?{{keyword}}. Pole {{keyword}} zamieniane jest z 
frazą podaną w programie.

Program ma opcję ustalenia ilości wątków procesora do działania programu. Im większa ilość wątków
tym bardziej obciążane jest łącze internetowe oraz bardziej obciążany jest procesor. Przy prędkości
5Mbps, 10 wątków procesora T4300, 3GB ram, 15 manualnych źródłach (lokalne wyszukiwarki), Google API
Custom Search (10 wyników) i dla słowa kluczowego "Warszawa" oraz limitem pod stron dla domeny 20
w ciągu 1 minuty przeanalizowano 500 stron internetowych (408 zawierało frazę, 85 unikatowych domen zawierało frazę)

Program ma opcję ograniczania przeszukiwania pod stron dla konkretnej domeny. Domyślnie
robot przeszukuje do 10 pod stron dla domeny. Wartość ta jest zmienna, im wyższa wartość tym analiza dłużej
trwa oraz analiza daje większe rezultaty.

Robot z każdej analizowanej strony internetowej pobiera adresy kolejnych stron internetowych do dalszej
analizy. Przeanalizowane strony są zapamiętywane. Dodatkowo limit pod stron domeny ogranicza ilość
pobranych adresów internetowych do dalszej analizy. Jeżeli wszystkie pobrane adresy zostaną
przeanalizowane, robot zakańcza pracę.

Po wyszukiwaniu domen internetowych dla frazy, usuwane są zdublowane pozycje, po czym wyniki są dodawane 
w zależności od wyboru do pliku txt w formacie JSON lub do bazy MySQL. Program sprawdza czy dany wynik 
istnieje w pliku txt lub bazie MySQL, tak aby nie powtarzać wyników co do domeny oraz frazy.

## License

![88x31](https://cloud.githubusercontent.com/assets/12084504/18874988/46d29e30-84c4-11e6-97c7-0ffc2229e0eb.png)
Creative Commons. Attribution-NonCommercial-NoDerivatives 3.0 International (CC BY-NC-ND 3.0)

## Before run application
- Install Chrome Web Browser in your system

## Configuration Sources
You can add urls manual to application. If Google Engine will not minimality 15 urls, then application
get urls from ```Sources tab```. Add  minimality 15 links for start job of robot. Each w new line. Example:
```
http://website.com?query={{keyword}}
http://websitetwo.com
```

If you not added, robot will analyzed keyword only search engine (if search engine will configured).
If will not minimality urls in sources and will not configured search engine, application will not analyzed keyword.

Search engine can be not configuration, but sources with urls mu be fill with minimality 15 urls.

The more urls in the ```Sources tab``` better results.

You may use ```{{keyword}}``` template to your manual urls.

## Configuration Google API
Go to your account in Google Service and create API Search Custom and create
your credentials for get API. Create in Google Service your Search Engine Custom for get CX ID.
In your Search Engine Custom, set option ```Websites for search``` to ```all```

In application set option ```Queries for keyword```. Default is ```1```. 
For FREE Queries in Google API, limit day in 100 queries.

Example: ```1 queries for keyword``` get 10 results links of keyword from Google Search Engine API.

## Configuration MySQL
Create table in your mysql server with 5 columns:
- for ```domains``` (String) [255]
- for ```url``` (Text)
- for ```quantity``` (Integer) [4]
- for ```date``` (Timestamp)
- for ```keyword``` (String) [100]

Your table name and column name add to configuration application in setting tab.

## Deploy

#### Requirements

- Java 8 or higher. Gradle must run with Java 8
- Wix Toolset or WixEdit must be installed
- Add variable ProgramFiles(x86) to system path when value is ProgramFiles directory of x86 architecture in your system Windows
- Add variable ProgramW6432 to system path when value is ProgramFiles directory of x64 architecture in your system Windows

#### Steps

For create *.msi run tasks of gradle:

- gradle clean
- gradle assemble
- gradle.build
- gradle copyLibs
- gradle copyDrivers
- gradle copyUrls
- gradle launch4j
- gradle msi create msi installer for architecture 32bit
- gradle msi64 create msi installer for architecture 64bit

## Extensions

#### Phone & Email
- Add to your database MySQL two column for phones and emails (TEXT Type)

## Add extensions

- Add get and set of new property to IResultModel
- Implements properties in models
- Add service with implement IExtension
- Add instance IExtension to Data Services for saved data
- Add extensions for save data to file in save method:
```
if (phoneEmailService.access()){
    s.setPhone((s.getPhone()==null) ? "" : s.getPhone());
    s.setEmail((s.getEmail()==null) ? "" : s.getEmail());
    pairs.add(String.format("\"phones\":\"%1$s\"", s.getPhone()));
    pairs.add(String.format("\"emails\":\"%1$s\"", s.getEmail()));
}
```
- Add extensions for other save data
- Create tab with interface
- Add extension in ParseService in htmlToResult method example:
```
if (this.phoneEmailService.access()){
    this.phoneEmailService.analyze(result, doc);
}
```
- Add call for clear and finish methods of extension
- Add to parse service method of before saved

