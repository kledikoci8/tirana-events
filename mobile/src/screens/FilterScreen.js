import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Switch,
  TextInput,
  StatusBar,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import Slider from '@react-native-community/slider';
import * as Location from 'expo-location';
import api from '../services/api';

const FilterScreen = ({ navigation, route }) => {
  const { onApplyFilters } = route.params || {};
  
  // Price filter
  const [priceRange, setPriceRange] = useState([0, 5000]);
  const [includeFree, setIncludeFree] = useState(false);
  
  // Distance filter
  const [maxDistance, setMaxDistance] = useState(10);
  
  // Time of day
  const [timeOfDay, setTimeOfDay] = useState(null); // MORNING, AFTERNOON, EVENING, LATE_NIGHT
  
  // Date range
  const [dateRange, setDateRange] = useState('THIS_WEEK'); // TODAY, THIS_WEEKEND, THIS_WEEK, CUSTOM
  
  // Accessibility
  const [wheelchairAccess, setWheelchairAccess] = useState(false);
  const [hearingLoop, setHearingLoop] = useState(false);
  const [seatedVenue, setSeatedVenue] = useState(false);
  
  // Indoor/Outdoor
  const [venueType, setVenueType] = useState(null); // 'indoor', 'outdoor', null
  
  // Categories
  const [categories, setCategories] = useState([]);
  const [selectedCategories, setSelectedCategories] = useState([]);

  useEffect(() => {
    fetchCategories();
  }, []);

  const fetchCategories = async () => {
    try {
      const response = await api.get('/categories');
      setCategories(response.data);
    } catch (error) {
      console.error('Error fetching categories:', error);
    }
  };

  const toggleCategory = (categoryId) => {
    if (selectedCategories.includes(categoryId)) {
      setSelectedCategories(selectedCategories.filter(id => id !== categoryId));
    } else {
      setSelectedCategories([...selectedCategories, categoryId]);
    }
  };

  const applyFilters = async () => {
    const filters = {
      minPrice: includeFree ? null : priceRange[0],
      maxPrice: includeFree ? null : priceRange[1],
      includeFree,
      maxDistance,
      startHour: getStartHour(timeOfDay),
      endHour: getEndHour(timeOfDay),
      dateRangeType: dateRange,
      requireWheelchairAccess: wheelchairAccess,
      requireHearingLoop: hearingLoop,
      requireSeatedVenue: seatedVenue,
      indoorOnly: venueType === 'indoor',
      outdoorOnly: venueType === 'outdoor',
      categoryIds: selectedCategories,
    };

    try {
      const { status } = await Location.requestForegroundPermissionsAsync();
      if (status === 'granted') {
        const loc = await Location.getCurrentPositionAsync({});
        filters.userLatitude = loc.coords.latitude;
        filters.userLongitude = loc.coords.longitude;
      }
      const response = await api.post('/filters/events', filters);
      
      if (onApplyFilters) {
        onApplyFilters(response.data);
      }
      navigation.goBack();
    } catch (error) {
      console.error('Error applying filters:', error);
      alert('Failed to apply filters');
    }
  };

  const resetFilters = () => {
    setPriceRange([0, 5000]);
    setIncludeFree(false);
    setMaxDistance(10);
    setTimeOfDay(null);
    setDateRange('THIS_WEEK');
    setWheelchairAccess(false);
    setHearingLoop(false);
    setSeatedVenue(false);
    setVenueType(null);
    setSelectedCategories([]);
  };

  const getStartHour = (time) => {
    const hours = {
      MORNING: 6,
      AFTERNOON: 12,
      EVENING: 18,
      LATE_NIGHT: 22,
    };
    return hours[time] || null;
  };

  const getEndHour = (time) => {
    const hours = {
      MORNING: 12,
      AFTERNOON: 18,
      EVENING: 22,
      LATE_NIGHT: 6,
    };
    return hours[time] || null;
  };

  return (
    <View style={styles.container}>
      <StatusBar barStyle="light-content" />
      
      {/* Header */}
      <View style={styles.header}>
        <TouchableOpacity onPress={() => navigation.goBack()}>
          <Ionicons name="close" size={24} color="#FFFFFF" />
        </TouchableOpacity>
        <Text style={styles.headerTitle}>Filters</Text>
        <TouchableOpacity onPress={resetFilters}>
          <Text style={styles.resetText}>Reset</Text>
        </TouchableOpacity>
      </View>

      <ScrollView style={styles.content} showsVerticalScrollIndicator={false}>
        {/* Price Range */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Price Range</Text>
          <View style={styles.priceRow}>
            <Text style={styles.priceLabel}>
              {includeFree ? 'Free Events Only' : `${priceRange[0]} - ${priceRange[1]} ALL`}
            </Text>
          </View>
          {!includeFree && (
            <Slider
              style={styles.slider}
              minimumValue={0}
              maximumValue={5000}
              step={100}
              value={priceRange[1]}
              onValueChange={(value) => setPriceRange([0, value])}
              minimumTrackTintColor="#7F77DD"
              maximumTrackTintColor="#3A3A4A"
              thumbTintColor="#7F77DD"
            />
          )}
          <View style={styles.switchRow}>
            <Text style={styles.switchLabel}>Free events only</Text>
            <Switch
              value={includeFree}
              onValueChange={setIncludeFree}
              trackColor={{ false: '#3A3A4A', true: '#7F77DD' }}
              thumbColor="#FFFFFF"
            />
          </View>
        </View>

        {/* Distance */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Distance</Text>
          <Text style={styles.distanceLabel}>Within {maxDistance}km</Text>
          <View style={styles.distanceButtons}>
            {[1, 3, 5, 10].map((dist) => (
              <TouchableOpacity
                key={dist}
                style={[styles.distanceButton, maxDistance === dist && styles.distanceButtonActive]}
                onPress={() => setMaxDistance(dist)}
              >
                <Text style={[styles.distanceButtonText, maxDistance === dist && styles.distanceButtonTextActive]}>
                  {dist}km
                </Text>
              </TouchableOpacity>
            ))}
          </View>
        </View>

        {/* Time of Day */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Time of Day</Text>
          <View style={styles.timeButtons}>
            {[
              { key: 'MORNING', label: 'Morning', icon: 'sunny' },
              { key: 'AFTERNOON', label: 'Afternoon', icon: 'partly-sunny' },
              { key: 'EVENING', label: 'Evening', icon: 'moon' },
              { key: 'LATE_NIGHT', label: 'Late Night', icon: 'moon-outline' },
            ].map((time) => (
              <TouchableOpacity
                key={time.key}
                style={[styles.timeButton, timeOfDay === time.key && styles.timeButtonActive]}
                onPress={() => setTimeOfDay(timeOfDay === time.key ? null : time.key)}
              >
                <Ionicons
                  name={time.icon}
                  size={20}
                  color={timeOfDay === time.key ? '#FFFFFF' : '#A0A0B0'}
                />
                <Text style={[styles.timeButtonText, timeOfDay === time.key && styles.timeButtonTextActive]}>
                  {time.label}
                </Text>
              </TouchableOpacity>
            ))}
          </View>
        </View>

        {/* Date Range */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Date Range</Text>
          <View style={styles.dateButtons}>
            {[
              { key: 'TODAY', label: 'Today' },
              { key: 'THIS_WEEKEND', label: 'This Weekend' },
              { key: 'THIS_WEEK', label: 'This Week' },
            ].map((date) => (
              <TouchableOpacity
                key={date.key}
                style={[styles.dateButton, dateRange === date.key && styles.dateButtonActive]}
                onPress={() => setDateRange(date.key)}
              >
                <Text style={[styles.dateButtonText, dateRange === date.key && styles.dateButtonTextActive]}>
                  {date.label}
                </Text>
              </TouchableOpacity>
            ))}
          </View>
        </View>

        {/* Accessibility */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Accessibility</Text>
          <View style={styles.switchRow}>
            <Text style={styles.switchLabel}>Wheelchair accessible</Text>
            <Switch
              value={wheelchairAccess}
              onValueChange={setWheelchairAccess}
              trackColor={{ false: '#3A3A4A', true: '#7F77DD' }}
              thumbColor="#FFFFFF"
            />
          </View>
          <View style={styles.switchRow}>
            <Text style={styles.switchLabel}>Hearing loop available</Text>
            <Switch
              value={hearingLoop}
              onValueChange={setHearingLoop}
              trackColor={{ false: '#3A3A4A', true: '#7F77DD' }}
              thumbColor="#FFFFFF"
            />
          </View>
          <View style={styles.switchRow}>
            <Text style={styles.switchLabel}>Seated venue</Text>
            <Switch
              value={seatedVenue}
              onValueChange={setSeatedVenue}
              trackColor={{ false: '#3A3A4A', true: '#7F77DD' }}
              thumbColor="#FFFFFF"
            />
          </View>
        </View>

        {/* Venue Type */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Venue Type</Text>
          <View style={styles.venueButtons}>
            <TouchableOpacity
              style={[styles.venueButton, venueType === 'indoor' && styles.venueButtonActive]}
              onPress={() => setVenueType(venueType === 'indoor' ? null : 'indoor')}
            >
              <Ionicons name="home" size={20} color={venueType === 'indoor' ? '#FFFFFF' : '#A0A0B0'} />
              <Text style={[styles.venueButtonText, venueType === 'indoor' && styles.venueButtonTextActive]}>
                Indoor
              </Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={[styles.venueButton, venueType === 'outdoor' && styles.venueButtonActive]}
              onPress={() => setVenueType(venueType === 'outdoor' ? null : 'outdoor')}
            >
              <Ionicons name="leaf" size={20} color={venueType === 'outdoor' ? '#FFFFFF' : '#A0A0B0'} />
              <Text style={[styles.venueButtonText, venueType === 'outdoor' && styles.venueButtonTextActive]}>
                Outdoor
              </Text>
            </TouchableOpacity>
          </View>
        </View>

        {/* Categories */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Categories</Text>
          <View style={styles.categoriesGrid}>
            {categories.map((category) => (
              <TouchableOpacity
                key={category.id}
                style={[
                  styles.categoryChip,
                  selectedCategories.includes(category.id) && styles.categoryChipActive
                ]}
                onPress={() => toggleCategory(category.id)}
              >
                <Text style={[
                  styles.categoryChipText,
                  selectedCategories.includes(category.id) && styles.categoryChipTextActive
                ]}>
                  {category.name}
                </Text>
              </TouchableOpacity>
            ))}
          </View>
        </View>
      </ScrollView>

      {/* Apply Button */}
      <View style={styles.footer}>
        <TouchableOpacity style={styles.applyButton} onPress={applyFilters}>
          <Text style={styles.applyButtonText}>Apply Filters</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#0A0A0F',
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 20,
    paddingTop: 60,
    paddingBottom: 20,
    borderBottomWidth: 1,
    borderBottomColor: '#1A1A24',
  },
  headerTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#FFFFFF',
  },
  resetText: {
    fontSize: 16,
    color: '#7F77DD',
    fontWeight: '600',
  },
  content: {
    flex: 1,
  },
  section: {
    padding: 20,
    borderBottomWidth: 1,
    borderBottomColor: '#1A1A24',
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginBottom: 16,
  },
  priceRow: {
    marginBottom: 12,
  },
  priceLabel: {
    fontSize: 16,
    color: '#7F77DD',
    fontWeight: '600',
  },
  slider: {
    width: '100%',
    height: 40,
  },
  switchRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 12,
  },
  switchLabel: {
    fontSize: 16,
    color: '#FFFFFF',
  },
  distanceLabel: {
    fontSize: 16,
    color: '#A0A0B0',
    marginBottom: 12,
  },
  distanceButtons: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  distanceButton: {
    flex: 1,
    paddingVertical: 12,
    marginHorizontal: 4,
    backgroundColor: '#1A1A24',
    borderRadius: 8,
    alignItems: 'center',
  },
  distanceButtonActive: {
    backgroundColor: '#7F77DD',
  },
  distanceButtonText: {
    fontSize: 14,
    fontWeight: '600',
    color: '#A0A0B0',
  },
  distanceButtonTextActive: {
    color: '#FFFFFF',
  },
  timeButtons: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    marginHorizontal: -4,
  },
  timeButton: {
    width: '48%',
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 12,
    marginHorizontal: '1%',
    marginBottom: 8,
    backgroundColor: '#1A1A24',
    borderRadius: 8,
  },
  timeButtonActive: {
    backgroundColor: '#7F77DD',
  },
  timeButtonText: {
    fontSize: 14,
    fontWeight: '600',
    color: '#A0A0B0',
    marginLeft: 8,
  },
  timeButtonTextActive: {
    color: '#FFFFFF',
  },
  dateButtons: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  dateButton: {
    flex: 1,
    paddingVertical: 12,
    marginHorizontal: 4,
    backgroundColor: '#1A1A24',
    borderRadius: 8,
    alignItems: 'center',
  },
  dateButtonActive: {
    backgroundColor: '#7F77DD',
  },
  dateButtonText: {
    fontSize: 14,
    fontWeight: '600',
    color: '#A0A0B0',
  },
  dateButtonTextActive: {
    color: '#FFFFFF',
  },
  venueButtons: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  venueButton: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 12,
    marginHorizontal: 4,
    backgroundColor: '#1A1A24',
    borderRadius: 8,
  },
  venueButtonActive: {
    backgroundColor: '#7F77DD',
  },
  venueButtonText: {
    fontSize: 14,
    fontWeight: '600',
    color: '#A0A0B0',
    marginLeft: 8,
  },
  venueButtonTextActive: {
    color: '#FFFFFF',
  },
  categoriesGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    marginHorizontal: -4,
  },
  categoryChip: {
    paddingHorizontal: 16,
    paddingVertical: 8,
    marginHorizontal: 4,
    marginBottom: 8,
    backgroundColor: '#1A1A24',
    borderRadius: 20,
    borderWidth: 1,
    borderColor: 'transparent',
  },
  categoryChipActive: {
    backgroundColor: '#7F77DD',
    borderColor: '#7F77DD',
  },
  categoryChipText: {
    fontSize: 14,
    fontWeight: '600',
    color: '#A0A0B0',
  },
  categoryChipTextActive: {
    color: '#FFFFFF',
  },
  footer: {
    padding: 20,
    borderTopWidth: 1,
    borderTopColor: '#1A1A24',
  },
  applyButton: {
    backgroundColor: '#7F77DD',
    borderRadius: 12,
    paddingVertical: 16,
    alignItems: 'center',
  },
  applyButtonText: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#FFFFFF',
  },
});

export default FilterScreen;
