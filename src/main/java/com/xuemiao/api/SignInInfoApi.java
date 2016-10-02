package com.xuemiao.api;

import com.xuemiao.api.Json.IdPasswordJson;
import com.xuemiao.exception.IdNotExistException;
import com.xuemiao.exception.PasswordErrorException;
import com.xuemiao.exception.SignInOrderException;
import com.xuemiao.model.pdm.AbsenceEntity;
import com.xuemiao.model.pdm.SignInInfoEntity;
import com.xuemiao.model.pdm.StudentIdAndOperDateKey;
import com.xuemiao.model.pdm.SysAdminEntity;
import com.xuemiao.model.repository.AbsenceRepository;
import com.xuemiao.model.repository.SignInInfoRepository;
import com.xuemiao.model.repository.SysAdminRepository;
import com.xuemiao.service.AdminValidationService;
import com.xuemiao.service.CookieValidationService;
import com.xuemiao.utils.DateUtils;
import com.xuemiao.utils.PasswordUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by dzj on 9/30/2016.
 */
@Path("/sign_in_info_api")
public class SignInInfoApi {
    private final String cookiePath = "/api/sign_in_info_api";
    @Autowired
    SignInInfoRepository signInInfoRepository;
    @Autowired
    AdminValidationService adminValidationService;
    @Autowired
    CookieValidationService cookieValidationService;
    @Autowired
    AbsenceRepository absenceRepository;
    @Autowired
    SysAdminRepository sysAdminRepository;
    @Value("${admin.cookie.token.age}")
    int adminCookieAge;

    @POST
    @Path("/test")
    public Response testCookie(@CookieParam("token") String tokenString) {
        Response loginResponse = cookieValidationService.checkTokenCookie(tokenString, 1);
        if (loginResponse != null) {
            return loginResponse;
        }
        return Response.ok().entity(tokenString).build();
    }

    private NewCookie getCookie(Long id) {
        return cookieValidationService.getTokenCookie(id, cookiePath, adminCookieAge);
    }

    private NewCookie refreshCookie(String tokenString) {
        return cookieValidationService.refreshCookie(tokenString, cookiePath, adminCookieAge);
    }

    @POST
    @Path("/admin/validation")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response adminValidation(IdPasswordJson idPasswordJson)
            throws IdNotExistException, PasswordErrorException {
        adminValidationService.testPassword(idPasswordJson.getId(), idPasswordJson.getPassword1(), 1);
        return Response.ok().cookie(getCookie(idPasswordJson.getId())).build();
    }

    @PUT
    @Path("/admin/password_update")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response adminPasswordUpdate(IdPasswordJson idPasswordJson)
            throws IdNotExistException, PasswordErrorException {
        adminValidationService.testPassword(idPasswordJson.getId(), idPasswordJson.getPassword1(), 1);
        SysAdminEntity sysAdminEntity = sysAdminRepository.findOne(idPasswordJson.getId());
        sysAdminEntity.setPasswordSalted(PasswordUtils.createPasswordHash(idPasswordJson.getPassword2()));
        sysAdminRepository.save(sysAdminEntity);
        return Response.ok().build();
    }

    @DELETE
    @Path("/admin/logout")
    public Response adminLogout(@CookieParam("token") String tokenString) {
        cookieValidationService.deleteCookieByToken(tokenString);
        return Response.ok().build();
    }

    @POST
    @Path("/sign_in_info/addition/{studentId}/{operDate}/{signInOrder}")
    public Response addSignIn(@PathParam("studentId") Long studentId,
                              @PathParam("operDate") String operDate,
                              @PathParam("signInOrder") int signInOrder
    )
            throws SignInOrderException {
        StudentIdAndOperDateKey studentIdAndOperDateKey = new StudentIdAndOperDateKey();
        studentIdAndOperDateKey.setStudentId(studentId);
        studentIdAndOperDateKey.setOperDate(new Date(DateUtils.parseDateString(operDate).getMillis()));
        SignInInfoEntity signInInfoEntity = signInInfoRepository.findOne(studentIdAndOperDateKey);
        Timestamp now = new Timestamp(DateTime.now().getMillis());
        switch (signInOrder) {
            case 1:
                signInInfoEntity.setStartMorning(now);
                break;
            case 2:
                signInInfoEntity.setEndMorning(now);
                break;
            case 3:
                signInInfoEntity.setStartAfternoon(now);
                break;
            case 4:
                signInInfoEntity.setEndAfternoon(now);
                break;
            case 5:
                signInInfoEntity.setStartNight(now);
                break;
            case 6:
                signInInfoEntity.setEndNight(now);
                break;
            default:
                throw new SignInOrderException();
        }
        signInInfoRepository.save(signInInfoEntity);
        return Response.ok().build();
    }

    @POST
    @Path("/absences/addition/{studentId}/{operDate}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addStudentAbsence(@PathParam("studentId") Long studentId,
                                      @PathParam("operDate") String operDate,
                                      AbsenceReasonJson absenceReasonJson,
                                      @CookieParam("token") String tokenString) {
        Response loginResponse = cookieValidationService.checkTokenCookie(tokenString, 1);
        if (loginResponse != null) {
            return loginResponse;
        }

        AbsenceEntity absenceEntity = new AbsenceEntity();
        absenceEntity.setStudentId(studentId);
        absenceEntity.setOperDate(new Date(DateUtils.parseDateString(operDate).getMillis()));
        absenceEntity.setAbsenceReason(absenceReasonJson.getReason());
        absenceEntity.setStartAbsence(Timestamp.valueOf(absenceReasonJson.getStartAbsence()));
        absenceEntity.setEndAbsence(Timestamp.valueOf(absenceReasonJson.getEndAbsence()));
        absenceRepository.save(absenceEntity);

        return Response.ok().cookie(refreshCookie(tokenString)).build();
    }

    private class AbsenceReasonJson {
        private String reason;
        private String startAbsence;
        private String endAbsence;

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getStartAbsence() {
            return startAbsence;
        }

        public void setStartAbsence(String startAbsence) {
            this.startAbsence = startAbsence;
        }

        public String getEndAbsence() {
            return endAbsence;
        }

        public void setEndAbsence(String endAbsence) {
            this.endAbsence = endAbsence;
        }
    }

}
