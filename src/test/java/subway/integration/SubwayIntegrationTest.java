package subway.integration;

import io.restassured.RestAssured;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import subway.dto.LineRequest;
import subway.dto.SectionRequest;
import subway.dto.StationRequest;
import subway.dto.StationResponse;
import subway.dto.SubwayResponse;

public class SubwayIntegrationTest extends IntegrationTest {
    
    @BeforeEach
    void setSubway() {
        
        final LineRequest lineRequest1 = new LineRequest("2호선", "blue");
        this.postLineRequest(lineRequest1);
        
        final StationRequest stationRequest = new StationRequest("강남역");
        this.postStationRequest(stationRequest);
        
        final StationRequest stationRequest2 = new StationRequest("잠실역");
        this.postStationRequest(stationRequest2);
        
        final StationRequest stationRequest3 = new StationRequest("성수역");
        this.postStationRequest(stationRequest3);
        
        final StationRequest stationRequest4 = new StationRequest("삼성역");
        this.postStationRequest(stationRequest4);
        
        final StationRequest stationRequest5 = new StationRequest("잠실새내역");
        this.postStationRequest(stationRequest5);
        
        final SectionRequest sectionRequest = new SectionRequest(1, 2, 1, "DOWN", 4);
        this.postSectionRequest(sectionRequest);
        
        final SectionRequest sectionRequest2 = new SectionRequest(1, 3, 1, "DOWN", 1);
        this.postSectionRequest(sectionRequest2);
        
        final SectionRequest sectionRequest3 = new SectionRequest(1, 4, 3, "DOWN", 1);
        this.postSectionRequest(sectionRequest3);
        
        final SectionRequest sectionRequest4 = new SectionRequest(1, 5, 4, "DOWN", 1);
        this.postSectionRequest(sectionRequest4);
        
        
    }
    
    @DisplayName("한 노선의 지하철 역들을 순서대로 출력한다")
    @Test
    void findAllStationsInLine() {
        final SubwayResponse subwayResponse = RestAssured.given().log().all()
                .when().get("/lines/1/stations")
                .then().log().all()
                .extract().as(SubwayResponse.class);
        
        final List<StationResponse> orderedStations = subwayResponse.getStationResponses();
        Assertions.assertThat(orderedStations.size()).isEqualTo(5);
        Assertions.assertThat(orderedStations.get(0).getName()).isEqualTo("강남역");
        Assertions.assertThat(orderedStations.get(1).getName()).isEqualTo("성수역");
        Assertions.assertThat(orderedStations.get(2).getName()).isEqualTo("삼성역");
        Assertions.assertThat(orderedStations.get(3).getName()).isEqualTo("잠실새내역");
        Assertions.assertThat(orderedStations.get(4).getName()).isEqualTo("잠실역");
        
    }
    
    @DisplayName("지하철 노선도를 출력한다")
    @Test
    void findAllLines() {
        final StationRequest stationRequest1 = new StationRequest("몽촌토성역");
        final StationRequest stationRequest2 = new StationRequest("석촌역");
        final LineRequest line = new LineRequest("8호선", "pink");
        
        this.postStationRequest(stationRequest1);
        
        this.postStationRequest(stationRequest2);
        
        this.postLineRequest(line);
        
        final SectionRequest sectionRequest1 = new SectionRequest(2, 6, 2, "", 1);
        final SectionRequest sectionRequest2 = new SectionRequest(2, 7, 2, "DOWN", 1);
        
        this.postSectionRequest(sectionRequest1);
        this.postSectionRequest(sectionRequest2);
        
        final List<SubwayResponse> subwayResponses = RestAssured.given().log().all()
                .when().get("/lines/stations")
                .then().log().all()
                .extract().jsonPath().getList(".", SubwayResponse.class);
        
        Assertions.assertThat(subwayResponses.size()).isEqualTo(2);
        Assertions.assertThat(subwayResponses.get(0).getLineResponse().getName()).isEqualTo("2호선");
        Assertions.assertThat(subwayResponses.get(0).getStationResponses().size()).isEqualTo(5);
        Assertions.assertThat(subwayResponses.get(1).getLineResponse().getName()).isEqualTo("8호선");
        Assertions.assertThat(subwayResponses.get(1).getStationResponses().size()).isEqualTo(3);
    }
    
    @DisplayName("만약 노선에 역이 아무것도 없는 경우 아무 역도 출력하지 않는다")
    @Test
    void findAllStationsInLineWithNoStations() {
        final LineRequest lineRequest = new LineRequest("3호선", "green");
        this.postLineRequest(lineRequest);
        
        final SubwayResponse subwayResponse = RestAssured.given().log().all()
                .when().get("/lines/2/stations")
                .then().log().all()
                .extract().as(SubwayResponse.class);
        
        final List<StationResponse> orderedStations = subwayResponse.getStationResponses();
        Assertions.assertThat(orderedStations.size()).isEqualTo(0);
    }
    
    private void postLineRequest(final LineRequest line) {
        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(line)
                .when().post("/lines")
                .then().log().all();
    }
    
    private void postStationRequest(final StationRequest stationRequest2) {
        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(stationRequest2)
                .when().post("/stations")
                .then().log().all();
    }
    
    private void postSectionRequest(final SectionRequest sectionRequest4) {
        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(sectionRequest4)
                .when().post("/sections")
                .then().log().all();
    }
}
