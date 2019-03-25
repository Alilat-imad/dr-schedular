package com.ositel.apiserver.Api.DtoViewModel.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilityMedecinResponse {
    private Long id;
    private String shiftHoraire;
    private boolean available;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        final AvailabilityMedecinResponse other = (AvailabilityMedecinResponse) obj;

        if (this.getId().intValue() == other.getId().intValue()) {
             return true;
        }else{
            return false;
        }

    }
}
