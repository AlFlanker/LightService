package com.vvvteam.yuglightservice.service.request.and.response.Responses;

import lombok.*;

import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
public class ResponceControlPoint {
    public ResponceControlPoint( long id, @NonNull String objectName, @NonNull Double latitude, @NonNull Double longitude) {
        this.id = id;
        this.objectName = objectName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @NonNull
    private long id;
    @NonNull
    private String objectName;
    @NonNull
    private Double latitude;
    @NonNull
    private Double longitude;
    @NonNull
    private Date lastUpdate;
    private WorkGroup4Map workGroup;
    private Organization4Map organization;


}
