package com.maxxenergy.edap.repository;

import com.maxxenergy.edap.model.SolarDataEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SolarDataEntryRepository extends MongoRepository<SolarDataEntry, String> {
    List<SolarDataEntry> findByUserIdOrderByTimestampDesc(String userId);
    List<SolarDataEntry> findByIsPublicTrueOrderByTimestampDesc();
    List<SolarDataEntry> findTop10ByUserIdOrderByTimestampDesc(String userId);
}