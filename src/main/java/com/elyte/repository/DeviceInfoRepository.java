package com.elyte.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.elyte.domain.DeviceInfo;
import java.util.List;


public interface DeviceInfoRepository extends JpaRepository<DeviceInfo,String>{

    List<DeviceInfo> findByUserUserid(String userid);
    
}
