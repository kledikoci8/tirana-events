import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  TouchableOpacity,
  TextInput,
  Image,
} from 'react-native';
import api from '../services/api';

const VIBE_TAGS = [
  'Great music',
  'Good vibes',
  'Too crowded',
  'Well organized',
  'Amazing venue',
  'Long queues',
  'Friendly staff',
  'Great atmosphere',
];

export default function ReviewScreen({ route, navigation }) {
  const { eventId } = route.params;
  const [reviews, setReviews] = useState([]);
  const [rating, setRating] = useState(0);
  const [comment, setComment] = useState('');
  const [selectedTags, setSelectedTags] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [averageRating, setAverageRating] = useState(0);

  useEffect(() => {
    loadReviews();
    loadAverageRating();
  }, []);

  const loadReviews = async () => {
    try {
      const response = await api.get(`/reviews/events/${eventId}`);
      setReviews(response.data);
    } catch (error) {
      console.error('Error loading reviews:', error);
    }
  };

  const loadAverageRating = async () => {
    try {
      const response = await api.get(`/reviews/events/${eventId}/rating`);
      setAverageRating(response.data.averageRating);
    } catch (error) {
      console.error('Error loading rating:', error);
    }
  };

  const submitReview = async () => {
    try {
      await api.post('/reviews', {
        eventId,
        rating,
        comment,
        vibeTags: selectedTags,
      });
      
      setShowForm(false);
      setRating(0);
      setComment('');
      setSelectedTags([]);
      loadReviews();
      loadAverageRating();
    } catch (error) {
      console.error('Error submitting review:', error);
    }
  };

  const toggleTag = (tag) => {
    if (selectedTags.includes(tag)) {
      setSelectedTags(selectedTags.filter((t) => t !== tag));
    } else {
      setSelectedTags([...selectedTags, tag]);
    }
  };

  const renderStars = (count, onPress) => (
    <View style={styles.starsRow}>
      {[1, 2, 3, 4, 5].map((star) => (
        <TouchableOpacity key={star} onPress={() => onPress && onPress(star)}>
          <Text style={styles.star}>{star <= count ? '★' : '☆'}</Text>
        </TouchableOpacity>
      ))}
    </View>
  );

  const renderReview = ({ item }) => (
    <View style={styles.reviewCard}>
      <View style={styles.reviewHeader}>
        <Image
          source={{ uri: item.userAvatar || 'https://via.placeholder.com/40' }}
          style={styles.avatar}
        />
        <View style={styles.reviewHeaderText}>
          <View style={styles.nameRow}>
            <Text style={styles.userName}>{item.userName}</Text>
            {item.isVerifiedAttendee && (
              <View style={styles.verifiedBadge}>
                <Text style={styles.verifiedText}>✓ Attended</Text>
              </View>
            )}
          </View>
          {renderStars(item.rating)}
        </View>
      </View>

      <Text style={styles.reviewComment}>{item.comment}</Text>

      {item.vibeTags && item.vibeTags.length > 0 && (
        <View style={styles.tagsRow}>
          {item.vibeTags.map((tag, index) => (
            <View key={index} style={styles.tag}>
              <Text style={styles.tagText}>{tag}</Text>
            </View>
          ))}
        </View>
      )}

      {item.organizerReply && (
        <View style={styles.organizerReply}>
          <Text style={styles.organizerLabel}>Organizer Reply:</Text>
          <Text style={styles.organizerReplyText}>{item.organizerReply}</Text>
        </View>
      )}

      <TouchableOpacity style={styles.helpfulButton}>
        <Text style={styles.helpfulText}>👍 Helpful ({item.helpfulCount})</Text>
      </TouchableOpacity>
    </View>
  );

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Reviews</Text>
        <View style={styles.ratingOverview}>
          {renderStars(Math.round(averageRating))}
          <Text style={styles.ratingText}>
            {averageRating.toFixed(1)} ({reviews.length} reviews)
          </Text>
        </View>
      </View>

      {!showForm && (
        <TouchableOpacity
          style={styles.writeReviewButton}
          onPress={() => setShowForm(true)}
        >
          <Text style={styles.writeReviewText}>Write a Review</Text>
        </TouchableOpacity>
      )}

      {showForm && (
        <View style={styles.reviewForm}>
          <Text style={styles.formLabel}>Your Rating</Text>
          {renderStars(rating, setRating)}

          <Text style={styles.formLabel}>Your Review</Text>
          <TextInput
            style={styles.commentInput}
            placeholder="Share your experience..."
            placeholderTextColor="#A0A0B0"
            multiline
            value={comment}
            onChangeText={setComment}
          />

          <Text style={styles.formLabel}>Vibe Tags</Text>
          <View style={styles.tagsGrid}>
            {VIBE_TAGS.map((tag) => (
              <TouchableOpacity
                key={tag}
                style={[styles.tagButton, selectedTags.includes(tag) && styles.tagButtonSelected]}
                onPress={() => toggleTag(tag)}
              >
                <Text
                  style={[styles.tagButtonText, selectedTags.includes(tag) && styles.tagButtonTextSelected]}
                >
                  {tag}
                </Text>
              </TouchableOpacity>
            ))}
          </View>

          <View style={styles.formButtons}>
            <TouchableOpacity
              style={styles.cancelButton}
              onPress={() => setShowForm(false)}
            >
              <Text style={styles.cancelButtonText}>Cancel</Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={[styles.submitButton, rating === 0 && styles.submitButtonDisabled]}
              onPress={submitReview}
              disabled={rating === 0}
            >
              <Text style={styles.submitButtonText}>Submit</Text>
            </TouchableOpacity>
          </View>
        </View>
      )}

      <FlatList
        data={reviews}
        renderItem={renderReview}
        keyExtractor={(item) => item.id.toString()}
        contentContainerStyle={styles.list}
      />
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
    fontSize: 28,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginBottom: 16,
  },
  ratingOverview: {
    alignItems: 'center',
  },
  ratingText: {
    fontSize: 16,
    color: '#A0A0B0',
    marginTop: 8,
  },
  starsRow: {
    flexDirection: 'row',
    gap: 4,
  },
  star: {
    fontSize: 24,
    color: '#FFD700',
  },
  writeReviewButton: {
    backgroundColor: '#7F77DD',
    margin: 16,
    padding: 16,
    borderRadius: 12,
    alignItems: 'center',
  },
  writeReviewText: {
    color: '#FFFFFF',
    fontSize: 16,
    fontWeight: 'bold',
  },
  reviewForm: {
    backgroundColor: '#1A1A24',
    margin: 16,
    padding: 16,
    borderRadius: 12,
  },
  formLabel: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginTop: 16,
    marginBottom: 8,
  },
  commentInput: {
    backgroundColor: '#0A0A0F',
    color: '#FFFFFF',
    padding: 12,
    borderRadius: 8,
    minHeight: 100,
    textAlignVertical: 'top',
  },
  tagsGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
  },
  tagButton: {
    backgroundColor: '#0A0A0F',
    paddingHorizontal: 12,
    paddingVertical: 8,
    borderRadius: 20,
    borderWidth: 1,
    borderColor: '#7F77DD',
  },
  tagButtonSelected: {
    backgroundColor: '#7F77DD',
  },
  tagButtonText: {
    color: '#7F77DD',
    fontSize: 14,
  },
  tagButtonTextSelected: {
    color: '#FFFFFF',
  },
  formButtons: {
    flexDirection: 'row',
    gap: 12,
    marginTop: 16,
  },
  cancelButton: {
    flex: 1,
    padding: 12,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#7F77DD',
    alignItems: 'center',
  },
  cancelButtonText: {
    color: '#7F77DD',
    fontSize: 16,
    fontWeight: 'bold',
  },
  submitButton: {
    flex: 1,
    backgroundColor: '#7F77DD',
    padding: 12,
    borderRadius: 8,
    alignItems: 'center',
  },
  submitButtonDisabled: {
    opacity: 0.5,
  },
  submitButtonText: {
    color: '#FFFFFF',
    fontSize: 16,
    fontWeight: 'bold',
  },
  list: {
    padding: 16,
  },
  reviewCard: {
    backgroundColor: '#1A1A24',
    borderRadius: 12,
    padding: 16,
    marginBottom: 16,
  },
  reviewHeader: {
    flexDirection: 'row',
    marginBottom: 12,
  },
  avatar: {
    width: 40,
    height: 40,
    borderRadius: 20,
    marginRight: 12,
  },
  reviewHeaderText: {
    flex: 1,
  },
  nameRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 4,
  },
  userName: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#FFFFFF',
    marginRight: 8,
  },
  verifiedBadge: {
    backgroundColor: '#4CAF50',
    paddingHorizontal: 8,
    paddingVertical: 2,
    borderRadius: 12,
  },
  verifiedText: {
    color: '#FFFFFF',
    fontSize: 12,
  },
  reviewComment: {
    fontSize: 14,
    color: '#FFFFFF',
    lineHeight: 20,
    marginBottom: 12,
  },
  tagsRow: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
    marginBottom: 12,
  },
  tag: {
    backgroundColor: '#7F77DD',
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 16,
  },
  tagText: {
    color: '#FFFFFF',
    fontSize: 12,
  },
  organizerReply: {
    backgroundColor: '#0A0A0F',
    padding: 12,
    borderRadius: 8,
    marginTop: 8,
  },
  organizerLabel: {
    fontSize: 12,
    fontWeight: 'bold',
    color: '#7F77DD',
    marginBottom: 4,
  },
  organizerReplyText: {
    fontSize: 14,
    color: '#FFFFFF',
  },
  helpfulButton: {
    marginTop: 8,
  },
  helpfulText: {
    fontSize: 14,
    color: '#A0A0B0',
  },
});
