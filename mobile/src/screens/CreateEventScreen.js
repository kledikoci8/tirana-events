import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TextInput,
  TouchableOpacity,
  Alert,
  StatusBar,
} from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons } from '@expo/vector-icons';
import api from '../services/api';

export default function CreateEventScreen({ navigation }) {
  const [categories, setCategories] = useState([]);
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    location: '',
    categoryId: null,
    startDate: '',
    imageUrl: '',
    maxAttendees: '',
  });

  useEffect(() => {
    loadCategories();
  }, []);

  const loadCategories = async () => {
    try {
      const response = await api.get('/categories');
      setCategories(response.data);
    } catch (error) {
      console.error('Error loading categories:', error);
    }
  };

  const handleCreate = async () => {
    if (!formData.name || !formData.location || !formData.categoryId) {
      Alert.alert('Error', 'Please fill in all required fields');
      return;
    }

    try {
      const eventData = {
        ...formData,
        startDate: new Date().toISOString(),
        maxAttendees: formData.maxAttendees ? parseInt(formData.maxAttendees) : null,
      };

      await api.post('/events', eventData);
      Alert.alert('Success', 'Event created successfully!');
      navigation.navigate('Home');
    } catch (error) {
      Alert.alert('Error', 'Failed to create event');
      console.error('Error creating event:', error);
    }
  };

  return (
    <View style={styles.container}>
      <StatusBar barStyle="light-content" />
      <LinearGradient colors={['#0A0A0F', '#1A1A24']} style={styles.gradient}>
        <ScrollView showsVerticalScrollIndicator={false}>
          <View style={styles.header}>
            <Text style={styles.title}>Create Event</Text>
            <Text style={styles.subtitle}>Share your event with the community</Text>
          </View>

          <View style={styles.form}>
            <View style={styles.uploadSection}>
              <TouchableOpacity style={styles.uploadButton}>
                <Ionicons name="image-outline" size={32} color="#8B5CF6" />
                <Text style={styles.uploadText}>Upload Event Photo</Text>
              </TouchableOpacity>
            </View>

            <Text style={styles.label}>Event Name *</Text>
            <TextInput
              style={styles.input}
              placeholder="Enter event name"
              placeholderTextColor="#6B7280"
              value={formData.name}
              onChangeText={(text) => setFormData({ ...formData, name: text })}
            />

            <Text style={styles.label}>Category *</Text>
            <ScrollView horizontal showsHorizontalScrollIndicator={false}>
              {categories.map((category) => (
                <TouchableOpacity
                  key={category.id}
                  style={[
                    styles.categoryChip,
                    formData.categoryId === category.id && styles.categoryChipActive,
                  ]}
                  onPress={() => setFormData({ ...formData, categoryId: category.id })}
                >
                  <Text style={styles.categoryChipText}>{category.name}</Text>
                </TouchableOpacity>
              ))}
            </ScrollView>

            <Text style={styles.label}>Location *</Text>
            <TextInput
              style={styles.input}
              placeholder="Enter event location"
              placeholderTextColor="#6B7280"
              value={formData.location}
              onChangeText={(text) => setFormData({ ...formData, location: text })}
            />

            <Text style={styles.label}>Date & Time</Text>
            <TouchableOpacity style={styles.dateInput}>
              <Ionicons name="calendar-outline" size={20} color="#9CA3AF" />
              <Text style={styles.dateText}>Select date and time</Text>
            </TouchableOpacity>

            <Text style={styles.label}>Description</Text>
            <TextInput
              style={[styles.input, styles.textArea]}
              placeholder="Tell people more about your event..."
              placeholderTextColor="#6B7280"
              value={formData.description}
              onChangeText={(text) => setFormData({ ...formData, description: text })}
              multiline
              numberOfLines={4}
            />

            <Text style={styles.label}>Max Attendees</Text>
            <TextInput
              style={styles.input}
              placeholder="Enter maximum number of attendees"
              placeholderTextColor="#6B7280"
              value={formData.maxAttendees}
              onChangeText={(text) => setFormData({ ...formData, maxAttendees: text })}
              keyboardType="numeric"
            />

            <TouchableOpacity style={styles.button} onPress={handleCreate}>
              <LinearGradient
                colors={['#8B5CF6', '#6D28D9']}
                style={styles.buttonGradient}
              >
                <Text style={styles.buttonText}>Publish Event</Text>
              </LinearGradient>
            </TouchableOpacity>
          </View>
        </ScrollView>
      </LinearGradient>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#0A0A0F',
  },
  gradient: {
    flex: 1,
  },
  header: {
    paddingHorizontal: 20,
    paddingTop: 60,
    paddingBottom: 24,
  },
  title: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#FFFFFF',
  },
  subtitle: {
    fontSize: 16,
    color: '#9CA3AF',
    marginTop: 4,
  },
  form: {
    paddingHorizontal: 20,
    paddingBottom: 40,
  },
  uploadSection: {
    marginBottom: 24,
  },
  uploadButton: {
    backgroundColor: '#1F1F2E',
    borderRadius: 16,
    padding: 40,
    alignItems: 'center',
    borderWidth: 2,
    borderColor: '#2A2A3C',
    borderStyle: 'dashed',
  },
  uploadText: {
    color: '#9CA3AF',
    marginTop: 8,
    fontSize: 14,
  },
  label: {
    fontSize: 14,
    fontWeight: '600',
    color: '#FFFFFF',
    marginBottom: 8,
    marginTop: 16,
  },
  input: {
    backgroundColor: '#1F1F2E',
    borderRadius: 12,
    padding: 16,
    color: '#FFFFFF',
    fontSize: 16,
    borderWidth: 1,
    borderColor: '#2A2A3C',
  },
  textArea: {
    height: 100,
    textAlignVertical: 'top',
  },
  categoryChip: {
    backgroundColor: '#1F1F2E',
    borderRadius: 20,
    paddingHorizontal: 16,
    paddingVertical: 8,
    marginRight: 8,
    borderWidth: 1,
    borderColor: '#2A2A3C',
  },
  categoryChipActive: {
    backgroundColor: '#8B5CF6',
    borderColor: '#8B5CF6',
  },
  categoryChipText: {
    color: '#FFFFFF',
    fontSize: 14,
  },
  dateInput: {
    backgroundColor: '#1F1F2E',
    borderRadius: 12,
    padding: 16,
    flexDirection: 'row',
    alignItems: 'center',
    borderWidth: 1,
    borderColor: '#2A2A3C',
  },
  dateText: {
    color: '#9CA3AF',
    fontSize: 16,
    marginLeft: 12,
  },
  button: {
    marginTop: 32,
    borderRadius: 12,
    overflow: 'hidden',
  },
  buttonGradient: {
    padding: 16,
    alignItems: 'center',
  },
  buttonText: {
    color: '#FFFFFF',
    fontSize: 16,
    fontWeight: 'bold',
  },
});
