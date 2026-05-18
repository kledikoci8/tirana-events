/**
 * Validation utilities for form inputs
 */

export const validateEmail = (email) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};

export const validatePassword = (password) => {
  // Minimum 6 characters (matching backend requirement)
  return password && password.length >= 6;
};

export const validateFullName = (name) => {
  return name && name.trim().length >= 2;
};

export const validateDistance = (distance) => {
  return distance && distance > 0 && distance <= 100;
};

export const validatePrice = (price) => {
  return price >= 0;
};

export const validatePriceRange = (minPrice, maxPrice) => {
  if (minPrice === null || minPrice === undefined) return true;
  if (maxPrice === null || maxPrice === undefined) return true;
  return minPrice <= maxPrice;
};

export const validateHour = (hour) => {
  return hour >= 0 && hour <= 23;
};

export const validateDateRange = (startDate, endDate) => {
  if (!startDate || !endDate) return true;
  return new Date(startDate) <= new Date(endDate);
};

export const validateCategories = (categories, min = 1, max = 10) => {
  return categories && categories.length >= min && categories.length <= max;
};

export const validateUrl = (url) => {
  try {
    new URL(url);
    return true;
  } catch (e) {
    return false;
  }
};

export const validatePhoneNumber = (phone) => {
  // Albanian phone number format
  const phoneRegex = /^(\+355|0)?[6-9]\d{8}$/;
  return phoneRegex.test(phone);
};

export const getValidationErrors = (formData) => {
  const errors = {};
  
  if (formData.email && !validateEmail(formData.email)) {
    errors.email = 'Invalid email address';
  }
  
  if (formData.password && !validatePassword(formData.password)) {
    errors.password = 'Password must be at least 6 characters';
  }
  
  if (formData.fullName && !validateFullName(formData.fullName)) {
    errors.fullName = 'Name must be at least 2 characters';
  }
  
  if (formData.minPrice !== undefined && formData.maxPrice !== undefined) {
    if (!validatePriceRange(formData.minPrice, formData.maxPrice)) {
      errors.priceRange = 'Min price cannot be greater than max price';
    }
  }
  
  return errors;
};
