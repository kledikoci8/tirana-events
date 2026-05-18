import React, { useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  TextInput,
  TouchableOpacity,
  FlatList,
  Image,
} from 'react-native';
import api from '../services/api';

const MOOD_BUTTONS = [
  { mood: 'ENERGETIC', emoji: '⚡', label: 'Energetic' },
  { mood: 'CHILL', emoji: '😌', label: 'Chill' },
  { mood: 'SOCIAL', emoji: '👥', label: 'Social' },
  { mood: 'CULTURAL', emoji: '🎭', label: 'Cultural' },
  { mood: 'ROMANTIC', emoji: '💕', label: 'Romantic' },
  { mood: 'ADVENTUROUS', emoji: '🏔️', label: 'Adventurous' },
];

export default function MoodSearchScreen({ navigation }) {
  const [query, setQuery] = useState('');
  const [results, setResults] = useState(null);
  const [loading, setLoading] = useState(false);

  const searchByMood = async (searchQuery) => {
    setLoading(true);
    try {
      const response = await api.post('/mood-search', { query: searchQuery });
      setResults(response.data);
    } catch (error) {
      console.error('Error:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleMoodButton = (mood) => {
    const queries = {
      ENERGETIC: 'I feel like dancing tonight',
      CHILL: 'I want something relaxing',
      SOCIAL: 'I want to meet new people',
      CULTURAL: 'Show me art and culture',
      ROMANTIC: 'Planning a romantic evening',
      ADVENTUROUS: 'I want an adventure',
    };
    const moodQuery = queries[mood];
    setQuery(moodQuery);
    searchByMood(moodQuery);
  };

  const renderEvent = ({ item }) => (
    <TouchableOpacity
      style={styles.eventCard}
      onPress={() => navigation.navigate('EventDetail', { eventId: item.id })}
    >
      <Image source={{ uri: item.imageUrl }} style={styles.eventImage} />
      <View style={styles.eventInfo}>
        <Text style={styles.eventName}>{item.name}</Text>
        <Text style={styles.venue}>{item.venue}</Text>
        <Text style={styles.price}>{item.price} ALL</Text>
      </View>
    </TouchableOpacity>
  );

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>How are you feeling?</Text>
        <Text style={styles.subtitle}>Tell us your mood and we'll find the perfect event</Text>
      </View>

      <View style={styles.searchContainer}>
        <TextInput
          style={styles.searchInput}
          placeholder="I feel like..."
          placeholderTextColor="#A0A0B0"
          value={query}
          onChangeText={setQuery}
          onSubmitEditing={() => searchByMood(query)}
        />
        <TouchableOpacity
          style={styles.searchButton}
          onPress={() => searchByMood(query)}
        >
          <Text style={styles.searchButtonText}>Search</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.moodGrid}>
        {MOOD_BUTTONS.map((mood) => (
          <TouchableOpacity
            key={mood.mood}
            style={styles.moodButton}
            onPress={() => handleMoodButton(mood.mood)}
          >
            <Text style={styles.moodEmoji}>{mood.emoji}</Text>
            <Text style={styles.moodLabel}>{mood.label}</Text>
          </TouchableOpacity>
        ))}
      </View>

      {results && (
        <View style={styles.resultsContainer}>
          <View style={styles.resultsHeader}>
            <Text style={styles.resultsTitle}>{results.interpretation}</Text>
            <Text style={styles.resultsMood}>Mood: {results.detectedMood}</Text>
          </View>

          <FlatList
            data={results.events}
            renderItem={renderEvent}
            keyExtractor={(item) => item.id.toString()}
            contentContainerStyle={styles.list}
            ListEmptyComponent={
              <Text style={styles.emptyText}>No events match your mood</Text>
            }
          />
        </View>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#0A0A0F',
  },
  header: {
    padding: 20,
    paddingTop: 60,
  },
  title: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 16,
    color: '#A0A0B0',
  },
  searchContainer: {
    flexDirection: 'row',
    padding: 16,
    gap: 12,
  },
  searchInput: {
    flex: 1,
    backgroundColor: '#1A1A24',
    color: '#FFFFFF',
    padding: 16,
    borderRadius: 12,
    fontSize: 16,
  },
  searchButton: {
    backgroundColor: '#7F77DD',
    paddingHorizontal: 24,
    borderRadius: 12,
    justifyContent: 'center',
  },
  searchButtonText: {
    color: '#FFFFFF',
    fontSize: 16,
    fontWeight: 'bold',
  },
  moodGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    padding: 16,
    gap: 12,
  },
  moodButton: {
    width: '30%',
    backgroundColor: '#1A1A24',
    padding: 20,
    borderRadius: 16,
    alignItems: 'center',
  },
  moodEmoji: {
    fontSize: 32,
    marginBottom: 8,
  },
  moodLabel: {
    fontSize: 14,
    color: '#FFFFFF',
    fontWeight: 'bold',
  },
  resultsContainer: {
    flex: 1,
  },
  resultsHeader: {
    padding: 16,
    backgroundColor: '#1A1A24',
    marginHorizontal: 16,
    borderRadius: 12,
    marginBottom: 16,
  },
  resultsTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginBottom: 4,
  },
  resultsMood: {
    fontSize: 14,
    color: '#7F77DD',
  },
  list: {
    padding: 16,
  },
  eventCard: {
    backgroundColor: '#1A1A24',
    borderRadius: 16,
    marginBottom: 16,
    overflow: 'hidden',
  },
  eventImage: {
    width: '100%',
    height: 150,
  },
  eventInfo: {
    padding: 16,
  },
  eventName: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginBottom: 4,
  },
  venue: {
    fontSize: 14,
    color: '#A0A0B0',
    marginBottom: 8,
  },
  price: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#7F77DD',
  },
  emptyText: {
    fontSize: 16,
    color: '#A0A0B0',
    textAlign: 'center',
    marginTop: 40,
  },
});
