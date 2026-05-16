import React from 'react';
import { ScrollView, StyleSheet } from 'react-native';
import Animated, {
  useAnimatedScrollHandler,
  useSharedValue,
} from 'react-native-reanimated';

const AnimatedScrollView = Animated.createAnimatedComponent(ScrollView);

export default function ParallaxScrollView({ children, onScroll, ...props }) {
  const scrollY = useSharedValue(0);

  const scrollHandler = useAnimatedScrollHandler({
    onScroll: (event) => {
      scrollY.value = event.contentOffset.y;
      onScroll?.(event);
    },
  });

  return (
    <AnimatedScrollView
      {...props}
      onScroll={scrollHandler}
      scrollEventThrottle={16}
      showsVerticalScrollIndicator={false}
    >
      {children}
    </AnimatedScrollView>
  );
}
