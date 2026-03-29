-- Create medical_history table
CREATE TABLE medical_history (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    diagnosis VARCHAR(200) NOT NULL,
    treatment TEXT,
    doctor_name VARCHAR(100),
    hospital_name VARCHAR(100),
    date_of_diagnosis DATE,
    date_of_recovery DATE,
    is_chronic BOOLEAN DEFAULT FALSE,
    is_allergy BOOLEAN DEFAULT FALSE,
    notes TEXT,
    medications TEXT,
    created_at DATE DEFAULT CURRENT_DATE,
    updated_at DATE DEFAULT CURRENT_DATE,
    CONSTRAINT fk_medical_history_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(id)
        ON DELETE CASCADE
);

-- Create vital_signs table
CREATE TABLE vital_signs (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    temperature DECIMAL(4,2),
    blood_pressure_systolic INTEGER,
    blood_pressure_diastolic INTEGER,
    heart_rate INTEGER,
    respiratory_rate INTEGER,
    oxygen_saturation DECIMAL(5,2),
    height DECIMAL(5,2),
    weight DECIMAL(5,2),
    bmi DECIMAL(5,2),
    blood_sugar DECIMAL(6,2),
    pain_level INTEGER CHECK (pain_level >= 0 AND pain_level <= 10),
    notes TEXT,
    recorded_by VARCHAR(100),
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vital_signs_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(id)
        ON DELETE CASCADE
);

-- Create indexes for medical_history table
CREATE INDEX idx_medical_history_patient_id ON medical_history(patient_id);
CREATE INDEX idx_medical_history_diagnosis ON medical_history(diagnosis);
CREATE INDEX idx_medical_history_date ON medical_history(date_of_diagnosis);
CREATE INDEX idx_medical_history_chronic ON medical_history(is_chronic);
CREATE INDEX idx_medical_history_allergy ON medical_history(is_allergy);

-- Create indexes for vital_signs table
CREATE INDEX idx_vital_signs_patient_id ON vital_signs(patient_id);
CREATE INDEX idx_vital_signs_recorded_at ON vital_signs(recorded_at);
CREATE INDEX idx_vital_signs_bmi ON vital_signs(bmi);
CREATE INDEX idx_vital_signs_blood_pressure ON vital_signs(blood_pressure_systolic, blood_pressure_diastolic);

-- Create function to calculate BMI
CREATE OR REPLACE FUNCTION calculate_bmi()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.height IS NOT NULL AND NEW.height > 0 AND NEW.weight IS NOT NULL AND NEW.weight > 0 THEN
        NEW.bmi = NEW.weight / ((NEW.height / 100.0) * (NEW.height / 100.0));
    END IF;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create trigger to automatically calculate BMI
CREATE TRIGGER calculate_vital_signs_bmi
    BEFORE INSERT OR UPDATE ON vital_signs
    FOR EACH ROW
    EXECUTE FUNCTION calculate_bmi();

-- Create function to update medical_history updated_at
CREATE OR REPLACE FUNCTION update_medical_history_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_DATE;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create trigger to automatically update medical_history updated_at
CREATE TRIGGER update_medical_history_updated_at
    BEFORE UPDATE ON medical_history
    FOR EACH ROW
    EXECUTE FUNCTION update_medical_history_updated_at();