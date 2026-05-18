import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  ScrollView,
  Dimensions,
  StatusBar,
} from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';
import AsyncStorage from '@react-native-async-storage/async-storage';
import api from '../services/api';

const { width } = Dimensions.get('window');

const OnboardingScreen = ({ navigation }) => {
  const [categories, setCategories] = useState([]);
  const [selectedCategories, setSelectedCategories] = useState([]);
  const [loading, setLoading] = useState(false);

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
      if (selectedCategories.length < 5) {
        setSelectedCategories([...selectedCategories, categoryId]);
      }
    }
  };

  const completeOnboarding = async () => {
    if (selectedCategories.length === 0) {
      alert('Please select at least one interest');
      return;
    }

    setLoading(true);
    try {
      await api.post('/personalization/onboarding/complete', {
        categoryIds: selectedCategories,
      });
      await AsyncStorage.setItem('onboardingCompleted', 'true');
      
      // Use reset instead of replace to ensure clean navigation
      navigation.reset({
        index: 0,
        routes: [{ name: 'Main' }],
      });
    } catch (error) {
      console.error('Error completing onboarding:', error);
      alert('Failed to save preferences. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const getCategoryIcon = (name) => {
    const icons = {
      'Music': 'musical-notes',
      'University': 'school',
      'Culture': 'color-palette',
      'Volunteering': 'heart',
      'Sports': 'football',
      'Food': 'restaurant',
      'Technology': 'laptop',
      'Art': 'brush',
    };
    return icons[name] || 'star';
  };

  return (
    <View style={styles.container}>
      <StatusBar barStyle="light-content" />
      
      <LinearGradient
        colors={['#0A0A0F', '#1A1A24']}
        style={styles.gradient}
      >
        <ScrollView 
          contentContainerStyle={styles.scrollContent}
          showsVerticalScrollIndicator={false}
        >
          {/* Header */}
          <View style={styles.header}>
            <Text style={styles.title}>Welcome to Tirana Events! 🎉</Text>
            <Text style={styles.subtitle}>
              Select up to 5 interests to personalize your feed
            </Text>
            <Text style={styles.counter}>
              {selectedCategories.length}/5 selected
            </Text>
          </View>

          {/* Categories Grid */}
          <View style={styles.categoriesGrid}>
            {categories.map((category) => {
              const isSelected = selectedCategories.includes(category.id);
              return (
                <TouchableOpacity
                  key={category.id}
                  style={[
                    styles.categoryCard,
                    isSelected && styles.categoryCardSelected
                  ]}
                  onPress={() => toggleCategory(category.id)}
                  activeOpacity={0.7}
                >
                  <View style={[
                    styles.iconContainer,
                    isSelected && styles.iconContainerSelected
                  ]}>
                    <Ionicons
                      name={getCategoryIcon(category.name)}
                      size={32}
                      color={isSelected ? '#FFFFFF' : '#7F77DD'}
                    />
                  </View>
                  <Text style={[
                    styles.categoryName,
                    isSelected && styles.categoryNameSelected
                  ]}>
                    {category.name}
                  </Text>
                  {isSelected && (
                    <View style={styles.checkmark}>
                      <Ionicons name="checkmark-circle" size={24} color="#7F77DD" />
                    </View>
                  )}
                </TouchableOpacity>
              );
            })}
          </View>

          {/* Continue Button */}
          <TouchableOpacity
            style={[
              styles.continueButton,
              selectedCategories.length === 0 && styles.continueButtonDisabled
            ]}
            onPress={completeOnboarding}
            disabled={loading || selectedCategories.length === 0}
          >
            <LinearGradient
              colors={selectedCategories.length > 0 ? ['#7F77DD', '#9B8FE8'] : ['#3A3A4A', '#3A3A4A']}
              style={styles.continueGradient}
              start={{ x: 0, y: 0 }}
              end={{ x: 1, y: 0 }}
            >
              <Text style={styles.continueText}>
                {loading ? 'Saving...' : 'Continue'}
              </Text>
              <Ionicons name="arrow-forward" size={20} color="#FFFFFF" />
            </LinearGradient>
          </TouchableOpacity>

          {/* Skip Button */}
          <TouchableOpacity
            style={styles.skipButton}
            onPress={async () => {
              await AsyncStorage.setItem('onboardingCompleted', 'true');
              navigation.reset({
                index: 0,
                routes: [{ name: 'Main' }],
              });
            }}
          >
            <Text style={styles.skipText}>Skip for now</Text>
          </TouchableOpacity>
        </ScrollView>
      </LinearGradient>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#0A0A0F',
  },
  gradient: {
    flex: 1,
  },
  scrollContent: {
    padding: 20,
    paddingTop: 60,
  },
  header: {
    marginBottom: 40,
  },
  title: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginBottom: 12,
  },
  subtitle: {
    fontSize: 16,
    color: '#A0A0B0',
    marginBottom: 8,
    lineHeight: 24,
  },
  counter: {
    fontSize: 14,
    color: '#7F77DD',
    fontWeight: '600',
  },
  categoriesGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-between',
    marginBottom: 40,
  },
  categoryCard: {
    width: (width - 60) / 2,
    backgroundColor: '#1A1A24',
    borderRadius: 16,
    padding: 20,
    marginBottom: 16,
    alignItems: 'center',
    borderWidth: 2,
    borderColor: 'transparent',
  },
  categoryCardSelected: {
    borderColor: '#7F77DD',
    backgroundColor: '#252535',
  },
  iconContainer: {
    width: 64,
    height: 64,
    borderRadius: 32,
    backgroundColor: '#252535',
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 12,
  },
  iconContainerSelected: {
    backgroundColor: '#7F77DD',
  },
  categoryName: {
    fontSize: 16,
    fontWeight: '600',
    color: '#FFFFFF',
    textAlign: 'center',
  },
  categoryNameSelected: {
    color: '#7F77DD',
  },
  checkmark: {
    position: 'absolute',
    top: 10,
    right: 10,
  },
  continueButton: {
    borderRadius: 12,
    overflow: 'hidden',
    marginBottom: 16,
  },
  continueButtonDisabled: {
    opacity: 0.5,
  },
  continueGradient: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: 16,
    paddingHorizontal: 24,
  },
  continueText: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginRight: 8,
  },
  skipButton: {
    alignItems: 'center',
    paddingVertical: 12,
  },
  skipText: {
    fontSize: 16,
    color: '#A0A0B0',
  },
});

export default OnboardingScreen;
