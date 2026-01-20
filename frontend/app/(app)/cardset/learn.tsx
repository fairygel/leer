import { useLocalSearchParams, useRouter } from 'expo-router';
import { useEffect, useState } from 'react';
import { ActivityIndicator, Pressable, ScrollView, Text, View } from 'react-native';
import { CardService } from '@/services/CardService';
import { FlashCard } from '@/types';
import { Colors } from '@/constants/Colors';
import { FONT_SIZES } from '@/constants/Sizes';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import Ionicons from '@expo/vector-icons/Ionicons';

export default function Learn() {
	const { setId } = useLocalSearchParams<{ setId: string }>();
	const router = useRouter();
	const insets = useSafeAreaInsets();
	const [cards, setCards] = useState<FlashCard[]>([]);
	const [loading, setLoading] = useState(true);

	useEffect(() => {
		if (setId) {
			CardService.getAll(setId)
				.then(setCards)
				.catch(console.error)
				.finally(() => setLoading(false));
		}
	}, [setId]);

	if (loading) {
		return (
			<View style={{ flex: 1, backgroundColor: Colors.secondary, justifyContent: 'center', alignItems: 'center' }}>
				<ActivityIndicator size="large" color={Colors.primary} />
			</View>
		);
	}

	return (
		<View style={{ flex: 1, backgroundColor: Colors.secondary, paddingTop: insets.top }}>
			<View style={{
				flexDirection: 'row',
				justifyContent: 'space-between',
				alignItems: 'center',
				paddingHorizontal: 24,
				paddingBottom: 24,
			}}>
				<Pressable onPress={() => router.back()}>
					<Ionicons name="arrow-back" size={FONT_SIZES.heading} color={Colors.primary} />
				</Pressable>
				<Text style={{
					fontSize: FONT_SIZES.heading,
					color: Colors.primary,
					fontWeight: 'bold',
				}}>Learn</Text>
				<View style={{ width: FONT_SIZES.heading }} />
			</View>

			<ScrollView style={{ flex: 1, paddingHorizontal: 24 }}>
				{cards.map(card => (
					<View key={card._id} style={{
						borderWidth: 2,
						borderColor: Colors.primary,
						borderRadius: 12,
						padding: 16,
						marginBottom: 12,
						backgroundColor: Colors.secondary,
					}}>
						<Text style={{
							fontSize: FONT_SIZES.body,
							color: Colors.primary,
							fontWeight: 'bold',
							marginBottom: 8,
						}}>{card.question}</Text>
						<View style={{
							height: 1,
							backgroundColor: Colors.primary,
							opacity: 0.2,
							marginVertical: 8,
						}} />
						<Text style={{
							fontSize: FONT_SIZES.body,
							color: Colors.primaryLighten,
						}}>{card.answer}</Text>
					</View>
				))}
			</ScrollView>
		</View>
	);
}

