import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '@store';
import { changePassword } from '@store/slices/authSlice';
import { toast } from 'react-hot-toast';

const ChangePassword: React.FC = () => {
  const dispatch = useDispatch();
  const { isLoading } = useSelector((state: RootState) => state.auth);

  const [formData, setFormData] = useState({
    currentPassword: '',
    newPassword: '',
    confirmNewPassword: '',
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (formData.newPassword !== formData.confirmNewPassword) {
      toast.error('New passwords do not match');
      return;
    }

    try {
      const resultAction = await dispatch(changePassword(formData));
      if (changePassword.fulfilled.match(resultAction)) {
        toast.success('Password changed successfully');
        setFormData({ currentPassword: '', newPassword: '', confirmNewPassword: '' });
      } else {
        toast.error(resultAction.payload as string || 'Failed to update password');
      }
    } catch (err) {
      toast.error('Error changing password');
    }
  };

  return (
    <div className="max-w-md mx-auto p-6 bg-white rounded-lg shadow">
      <h2 className="text-2xl font-bold mb-4">Change Password</h2>
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700">Current Password</label>
          <input
            name="currentPassword"
            type="password"
            value={formData.currentPassword}
            onChange={handleChange}
            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm"
            required
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700">New Password</label>
          <input
            name="newPassword"
            type="password"
            value={formData.newPassword}
            onChange={handleChange}
            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm"
            required
          />
        </div>
        <div>
          <label className="block text-sm font-medium text-gray-700">Confirm New Password</label>
          <input
            name="confirmNewPassword"
            type="password"
            value={formData.confirmNewPassword}
            onChange={handleChange}
            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm"
            required
          />
        </div>
        <button
          type="submit"
          disabled={isLoading}
          className="w-full py-2 px-4 rounded-md bg-blue-600 text-white hover:bg-blue-700 disabled:opacity-50"
        >
          {isLoading ? 'Updating...' : 'Update Password'}
        </button>
      </form>
    </div>
  );
};

export default ChangePassword;
