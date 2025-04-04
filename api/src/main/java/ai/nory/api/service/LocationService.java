package ai.nory.api.service;

import ai.nory.api.dto.LocationDto;
import ai.nory.api.entity.Location;
import ai.nory.api.mapper.LocationDtoMapper;
import ai.nory.api.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepository locationRepository;

    public LocationDto getLocation(Long locationId) {
        Optional<Location> locationOptional = locationRepository.findById(locationId);
        if (locationOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found for id: " + locationId);
        }

        return LocationDtoMapper.INSTANCE.fromEntity(locationOptional.get());
    }
}
