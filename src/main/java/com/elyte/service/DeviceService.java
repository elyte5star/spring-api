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
import java.net.InetAddress;
import java.util.Objects;
import com.elyte.domain.DeviceInfo;
import com.elyte.domain.User;
import com.elyte.domain.request.EmailAlert;
import com.elyte.repository.DeviceInfoRepository;
import com.elyte.utils.UtilityFunctions;
import com.google.common.base.Strings;
import java.util.List;
import java.util.Locale;
import java.io.IOException;

@Component
public class DeviceService extends UtilityFunctions {

    private static final String UNKNOWN = "UNKNOWN";

    @Autowired
    private EmailAlertService emailAlertService;

    @Autowired
    private DeviceInfoRepository deviceInfoRepository;

    @Autowired
    private Parser parser;

    @Autowired
    @Qualifier("GeoIPCity")
    private DatabaseReader databaseReader;

    public void verifyDevice(User user, HttpServletRequest request) throws IOException, GeoIp2Exception {
        String ip = extractIp(request);
        String location = getIpLocation(ip);
        String deviceDetails = getDeviceInfo(request.getHeader("user-agent"));
        DeviceInfo exitDeviceInfo = findKnownDevice(user.getUserid(), deviceDetails, location);
        if (Objects.isNull(exitDeviceInfo)) {
            unKnownDeviceLoginNotification(user, deviceDetails, location, ip, user.getEmail(), request.getLocale());
            DeviceInfo deviceInfo = new DeviceInfo();
            deviceInfo.setUser(user);
            deviceInfo.setLocation(location);
            deviceInfo.setDeviceDetails(deviceDetails);
            deviceInfo.setLastLoginDate(this.timeNow());
            deviceInfoRepository.save(deviceInfo);

        } else {
            exitDeviceInfo.setLastLoginDate(this.timeNow());
            deviceInfoRepository.save(exitDeviceInfo);

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

        return clientIp;
    }

    private String parseXForwardedHeader(String header) {
        return header.split(" *, *")[0];
    }

    private String getIpLocation(String ip) throws IOException, GeoIp2Exception {
        String location = UNKNOWN;
        InetAddress ipAddress = InetAddress.getByName("128.101.101.101");

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
        String text = "Location: " + location + " Device details: " + deviceDetails + " Ip Address: " + ip;
        EmailAlert mailObject = new EmailAlert(user.getEmail(), user.getUsername(), "New Login Notification");
        emailAlertService.sendStringMail(mailObject, text, locale);

    }

}
