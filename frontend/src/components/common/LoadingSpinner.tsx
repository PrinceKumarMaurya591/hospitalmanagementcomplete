import React from 'react';

interface LoadingSpinnerProps {
  size?: 'small' | 'medium' | 'large';
  color?: string;
}

const LoadingSpinner: React.FC<LoadingSpinnerProps> = ({
  size = 'medium',
  color = 'blue'
}) => {
  const sizeClasses = {
    small: 'h-4 w-4',
    medium: 'h-8 w-8',
    large: 'h-12 w-12',
  };

  const colorClasses = {
    blue: 'text-blue-600',
    gray: 'text-gray-600',
    white: 'text-white',
    green: 'text-green-600',
    red: 'text-red-600',
  };

  return (
    <div className="flex items-center justify-center">
      <div
        className={`animate-spin rounded-full border-2 border-gray-300 border-t-2 ${sizeClasses[size]} ${colorClasses[color as keyof typeof colorClasses] || colorClasses.blue}`}
        style={{ borderTopColor: 'currentColor' }}
      />
    </div>
  );
};

export default LoadingSpinner;