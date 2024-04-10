package com.elyte.service;

import org.springframework.beans.factory.annotation.Autowired;
import ua_parser.Client;
import ua_parser.Parser;
import org.springframework.stereotype.Component;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import java.net.InetAddress;
import java.util.Objects;
import com.elyte.domain.DeviceInfo;
import com.elyte.domain.User;
import com.elyte.domain.enums.EmailType;
import com.elyte.domain.request.EmailAlert;
import com.elyte.repository.DeviceInfoRepository;
import com.elyte.security.events.GeneralUserEvent;
import com.elyte.utils.UtilityFunctions;
import com.google.common.base.Strings;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.io.IOException;

@Component
public class DeviceService extends UtilityFunctions {

    private static final String UNKNOWN = "UNKNOWN";

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private DeviceInfoRepository deviceInfoRepository;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private Parser parser;

    @Autowired
    @Qualifier("GeoIPCity")
    private DatabaseReader databaseReader;

    public void verifyDevice(User user, HttpServletRequest request) throws IOException, GeoIp2Exception {
        String ip = extractIp(request);
        String location = getIpLocation(ip);
        String deviceDetails = getDeviceInfo(request.getHeader("user-agent"));
        DeviceInfo existDeviceInfo = findKnownDevice(user.getUserid(), deviceDetails, location);
        if (Objects.isNull(existDeviceInfo)) {
            unKnownDeviceLoginNotification(user, deviceDetails, location, ip, user.getEmail(), request.getLocale());
            DeviceInfo deviceInfo = new DeviceInfo();
            deviceInfo.setUser(user);
            deviceInfo.setLocation(location);
            deviceInfo.setDeviceDetails(deviceDetails);
            deviceInfo.setLastLoginDate(this.timeNow());
            deviceInfoRepository.save(deviceInfo);

        } else {
            existDeviceInfo.setLastLoginDate(this.timeNow());
            deviceInfoRepository.save(existDeviceInfo);

        }

    }

    private String extractIp(HttpServletRequest request) {
        String clientIp;
        String clientXForwardedForIp = request.getHeader("x-forwarded-for");
        if (Objects.nonNull(clientXForwardedForIp)) {
            clientIp = parseXForwardedHeader(clientXForwardedForIp);
        } else {
            clientIp = request.getRemoteAddr();
        }
        // return "128.101.101.101"; // for testing Richfield,United States
        // return "41.238.0.198"; // for testing Giza, Egypt

        return clientIp;
    }

    private String parseXForwardedHeader(String header) {
        return header.split(" *, *")[0];
    }

    private String getIpLocation(String ip) throws IOException, GeoIp2Exception {
        String location = "LocalHost";
        if (this.checkIfLocalHost(ip))
            return location;
        InetAddress ipAddress = InetAddress.getByName(ip);
        CityResponse cityResponse = databaseReader.city(ipAddress);
        if (Objects.nonNull(cityResponse) &&
                Objects.nonNull(cityResponse.getCity()) &&
                !Strings.isNullOrEmpty(cityResponse.getCity().getName())) {
            location = cityResponse.getCity().getName();
        }

        return location;
    }

    private String getDeviceInfo(String userAgent) {
        String deviceDetails = UNKNOWN;
        Client client = parser.parse(userAgent);
        if (Objects.nonNull(client)) {
            deviceDetails = client.userAgent.family + " " + client.userAgent.major + "." + client.userAgent.minor +
                    " - " + client.os.family + " " + client.os.major + "." + client.os.minor;
        }
        return deviceDetails;
    }

    private DeviceInfo findKnownDevice(String userid, String deviceDetails, String location) {
        List<DeviceInfo> existingDevices = deviceInfoRepository.findByUserUserid(userid);
        for (DeviceInfo existingDevice : existingDevices) {
            if (existingDevice.getDeviceDetails().equals(deviceDetails)
                    && existingDevice.getLocation().equals(location)) {
                return existingDevice;
            }

        }
        return null;
    }

    private void unKnownDeviceLoginNotification(User user, String deviceDetails, String location, String ip,
            String email, Locale locale) {
        String text = "Location: " + location + ", Device details: " + deviceDetails + ", Ip Address: " + ip
                + "\n\nRegards,\nTeam ELYTE.\n\n\nThis is system generated mail. Please do not reply to this.";
        EmailAlert emailAlert = new EmailAlert();
        emailAlert.setEmailType(EmailType.NEW_DEVICE_LOGIN);
        emailAlert.setRecipientEmail(user.getEmail());
        emailAlert.setRecipientUsername(user.getUsername());
        emailAlert.setSubject("New Device Login Notification");
        emailAlert.setData(Map.of("text", text));
        eventPublisher.publishEvent(new GeneralUserEvent(emailAlert, user, request.getLocale()));

    }

}
