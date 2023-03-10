package gr.pfizer.team5.sacchonapp.service;

import gr.pfizer.team5.sacchonapp.model.Users;
import gr.pfizer.team5.sacchonapp.dto.WarningDto;
import gr.pfizer.team5.sacchonapp.repository.BGLRepository;
import gr.pfizer.team5.sacchonapp.repository.DCIRepository;
import gr.pfizer.team5.sacchonapp.dto.BGL_Dto;
import gr.pfizer.team5.sacchonapp.dto.DCI_Dto;
import gr.pfizer.team5.sacchonapp.exception.CustomException;
import gr.pfizer.team5.sacchonapp.model.BloodGlucoseLevel;
import gr.pfizer.team5.sacchonapp.model.DailyCarbonatesIntake;
import gr.pfizer.team5.sacchonapp.dto.PatientDto;
import gr.pfizer.team5.sacchonapp.model.Patient;
import gr.pfizer.team5.sacchonapp.repository.PatientRepository;
import gr.pfizer.team5.sacchonapp.repository.UsersRepository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MediDataVaultServicesImpl implements MediDataVaultServices {
    private final BGLRepository BGLRepository;
    private final DCIRepository DCIRepository;
    private final PatientRepository patientRepository;
    private final UsersRepository usersRepository;


    //------------------------------------------------------start of BGL and DCI methods -------------------------------------------------//

    @Override
    public BGL_Dto createBGL(BGL_Dto bgl_dto) {
        BloodGlucoseLevel bloodGlucoseLevel = bgl_dto.asBGL();
        return new BGL_Dto(BGLRepository.save(bloodGlucoseLevel));
    }

    @Override

    public List<BGL_Dto> readBGL() {
        return BGLRepository
                .findAll()
                .stream()
                .map(BGL_Dto::new)
                .collect(Collectors.toList());
    }

    @Override

    public BGL_Dto readBGL(int id) throws CustomException {
        return new BGL_Dto(readBGL_DB(id));
    }

    private BloodGlucoseLevel readBGL_DB(int id) throws CustomException {
        Optional<BloodGlucoseLevel> bglOptional = BGLRepository.findById(id);
        if (bglOptional.isPresent())
            return bglOptional.get();
        throw new CustomException("Record not found");
    }

    @Override

    public boolean updateBGL(BGL_Dto bgl_dto, int id) {
        boolean action;
        try {
            BloodGlucoseLevel dbBGL = readBGL_DB(id);
            dbBGL.setDate(bgl_dto.getDate());
            dbBGL.setTime(bgl_dto.getTime());
            dbBGL.setMeasurement(bgl_dto.getMeasurement());
//            Patient patient = patientRepository.findById(bgl_dto.getPatientId())
//                    .orElseThrow(() -> new RecordNotFoundException("Patient not found"));
//            dbBGL.setPatient(patient);
            BGLRepository.save(dbBGL);
            action = true;
        } catch (CustomException e) {
            action = false;
        }
        return action;

    }
    @Override
    public boolean deleteBGL(int id) {
        boolean action;
        try {
            BloodGlucoseLevel dbBGL = readBGL_DB(id);
            BGLRepository.delete(dbBGL);
            action = true;
        }catch(CustomException e){
            action = false;
        }
        return action;
    }

    //DCI CRU Services
    @Override

    public DCI_Dto createDCI(DCI_Dto dci_dto) {
        DailyCarbonatesIntake dailyCarbonatesIntake = dci_dto.asDCI();
        return new DCI_Dto(DCIRepository.save(dailyCarbonatesIntake));
    }

    @Override

    public List<DCI_Dto> readDCI() {

        return DCIRepository
                .findAll()
                .stream()
                .map(DCI_Dto::new)
                .collect(Collectors.toList());
    }

    @Override

    public DCI_Dto readDCI(int id) throws CustomException {
        return new DCI_Dto(readDCI_DB(id));

    }

    private DailyCarbonatesIntake readDCI_DB(int id) throws CustomException {
        Optional<DailyCarbonatesIntake> dciOptional = DCIRepository.findById(id);
        if (dciOptional.isPresent())
            return dciOptional.get();
        throw new CustomException("Record not found");
    }

    @Override
    public boolean updateDCI(DCI_Dto dci_dto, int id) {
        boolean action;
        try {
            DailyCarbonatesIntake dbDCI = readDCI_DB(id);
            dbDCI.setDate(dci_dto.getDate());
            dbDCI.setTime(dci_dto.getTime());
            dbDCI.setMeasurement(dci_dto.getMeasurement());
//            Patient patient = patientRepository.findById(dci_dto.getPatientId())
//                    .orElseThrow(() -> new RecordNotFoundException("Patient not found"));
//            dbDCI.setPatient(patient);
            DCIRepository.save(dbDCI);
            action = true;
        } catch (CustomException e) {
            action = false;
        }
        return action;
    }

    @Override
    public boolean deleteDCI(int id) {
        boolean action;
        try {
            DailyCarbonatesIntake dbDCI = readDCI_DB(id);
            DCIRepository.delete(dbDCI);
            action = true;
        }catch(CustomException e){
            action = false;
        }
        return action;
    }



    public Double getAverageDCIBetweenDates(int id,LocalDate startDate, LocalDate endDate) {
        List<DailyCarbonatesIntake> dciList = DCIRepository.findBetweenDatesDCI(id,startDate, endDate);
        if (dciList == null || dciList.isEmpty()) {
            return null;
        }
        Double sum = 0.0;
        for (DailyCarbonatesIntake dci : dciList) {
            sum += dci.getMeasurement();
        }
        return sum / dciList.size();
    }


    @Override
    public Double getAverageBGLBetweenDates(int id,LocalDate startDate, LocalDate endDate) {
        List<BloodGlucoseLevel> bglList = BGLRepository.findBetweenDatesBGL(id,startDate, endDate);
        if (bglList == null || bglList.isEmpty()) {
            return null;
        }
        Double sum = 0.0;
        for (BloodGlucoseLevel bgl : bglList) {
            sum += bgl.getMeasurement();
        }
        return sum / bglList.size();
    }
//    @Override
//    public long isFirstAndLastRecordWithin30Days(int patientId, String recordType) throws CustomException {
//        switch (recordType) {
//            case "DCI":
//                List<DailyCarbonatesIntake> dciRecords = DCIRepository.findAllByPatientIdOrderByDateAsc(patientId);
//                if (DCIRepository.hasOnlyOneRecord(patientId) || dciRecords.size() < 2) {
//                    return -1;
//                }
//                LocalDate dciFirstDate = dciRecords.get(0).getDate();
//                LocalDate dciLastDate = dciRecords.get(dciRecords.size() - 1).getDate();
//                return ChronoUnit.DAYS.between(dciFirstDate, dciLastDate);
//
//            case "BGL":
//                List<BloodGlucoseLevel> bglRecords = BGLRepository.findAllByPatientIdOrderByDateAsc(patientId);
//                if (BGLRepository.hasOnlyOneRecord(patientId) || bglRecords.size() < 2) {
//                    return -1;
//                }
//                LocalDate bglFirstDate = bglRecords.get(0).getDate();
//                LocalDate bglLastDate = bglRecords.get(bglRecords.size() - 1).getDate();
//                return ChronoUnit.DAYS.between(bglFirstDate, bglLastDate);
//            default:
//                throw new CustomException("Invalid record type: " + recordType);
//        }
//    }
    @Override
    public long numberOfRecordings(int patientId){
        long bgl_records =  BGLRepository.getBGLRecordsOfPatient(patientId).size();
        long dci_records =  DCIRepository.getDCIRecordsOfPatient(patientId).size();
        long totalRecords = dci_records + bgl_records;
        return totalRecords;
    }
    @Override
    public boolean enoughRecordingsCheck(int patientId) throws CustomException {
        PatientDto patient = readPatient(patientId);
            if(numberOfRecordings(patientId)>=30)
            {
                patient.setHasRecordings(true);
                updatePatient(patient,patientId);
            }
            return patient.isHasRecordings();
    }

    @Override
    public long checkLowRecordingsExist(int patientId){
    return BGLRepository.checkLowRecordsExist(patientId);
    }

//------------------------------------------------------end of BGL and DCI methods -------------------------------------------------//

    @Override
    public boolean loginPatient(PatientDto patientDto) {
           return  usersRepository.existsUsersByUsernameAndPassword(patientDto.getUsername(), patientDto.getPassword());
    }

    @Override
    //second way, singing up users in the already created endpoints
    public PatientDto createPatient(PatientDto patientDto) throws CustomException {
        if (!usersRepository.existsUsersByUsername(patientDto.getUsername())){
            Users user = new Users(patientDto.getUsername(),patientDto.getPassword(),patientDto.getAuthority());
            Patient patient = patientDto.asPatient();
            patient.setUser(user);
            return new PatientDto(patientRepository.save(patient));
        } else{
        throw new CustomException("Username already exists");}
    }

    @Override
    public List<PatientDto> readPatient() {

        return patientRepository
                .findAll()
                .stream()
                .map(PatientDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public PatientDto readPatient(int id) throws CustomException {
        return new PatientDto(readPatientData(id));
    }
    private Patient readPatientData(int id) throws CustomException {
        Optional<Patient> patientOptional = patientRepository.findById(id);
        if (patientOptional.isPresent())
            return patientOptional.get();
        throw new CustomException("Patient: "+ id+ "not found");
    }

    @Override
    public boolean updatePatient(PatientDto patient, int id) {
        boolean action;
        try{
            Patient patientDb= readPatientData(id);
            patientDb.setUsername(patient.getUsername());
            patientDb.setPassword(patient.getPassword());
            patientDb.setFirstName(patient.getFirstName());
            patientDb.setLastName(patient.getLastName());
            patientDb.setAmkaCode(patient.getAmkaCode());
            patient.setDateOfBirth(patient.getDateOfBirth());
            patientRepository.save(patientDb);
            action = true;
        } catch (CustomException e){
            action = false;
        }
        return action;
    }

    @Override
    public boolean deletePatient(int id) {
        boolean action;
        try {
            Patient patientDb = readPatientData(id);
            patientRepository.delete(patientDb);
            action = true;
        }catch(CustomException e){
            action = false;
        }
        return action;
    }

    public void updateWarning(int id) {
        try {
            Patient p = readPatientData(id);
            p.setWarning_modifiedconsultation(true);
            patientRepository.save(p);
        }catch(CustomException e){
        }
    }

    @Override
    public WarningDto warnPatientAboutModifiedConsultation(int id) {
        WarningDto warning = new WarningDto();
        try {
            Patient p = readPatientData(id);
            if (p.isWarning_modifiedconsultation())
                warning.setWarningMessage("Your Doctor modified a consultation. Important information must be reviewed");
        } catch (CustomException e) { }
            return warning;
    }





}
