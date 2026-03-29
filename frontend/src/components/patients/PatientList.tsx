import React from 'react';

const PatientList: React.FC = () => {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">Patients</h1>
        <button className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700">
          Add Patient
        </button>
      </div>

      <div className="bg-white shadow rounded-lg">
        <div className="px-4 py-5 sm:p-6">
          <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">
            Patient Management
          </h3>
          <p className="text-gray-500">
            Patient list and management functionality will be implemented here.
          </p>
        </div>
      </div>
    </div>
  );
};

export default PatientList;