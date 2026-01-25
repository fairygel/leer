import { useLocalSearchParams, useRouter } from 'expo-router';
import { useEffect, useState } from 'react';
import { ActivityIndicator, Pressable, Text, View } from 'react-native';
import { CardService } from '@/services/CardService';
import { CardStatus, FlashCard } from '@/types';
import { Colors } from '@/constants/Colors';
import { FONT_SIZES } from '@/constants/Sizes';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import Ionicons from '@expo/vector-icons/Ionicons';
import { Entypo } from '@expo/vector-icons';

export default function Learn() {
	const { setId } = useLocalSearchParams<{ setId: string }>();
	const router = useRouter();
	const insets = useSafeAreaInsets();
	const [cards, setCards] = useState<FlashCard[]>([]);
	const [loading, setLoading] = useState(true);

	let [currentCardIndex, setCurrentCardIndex] = useState(0);
	let [showAnswer, setShowAnswer] = useState(false);

	useEffect(() => {
		if (setId) {
			CardService.getAll(setId)
				.then(async allCards => {
					let cards = allCards.filter(c => c.status === CardStatus.NEW);
					if (cards.length === 0) {
						cards = allCards;
					}

					await Promise.all(cards.map(c => {
						c.status = CardStatus.NEW;
						return CardService.update(c);
					}));

					setCards(cards)
				})
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

	function getCardContent() {
		return showAnswer ? cards[currentCardIndex].answer : cards[currentCardIndex].question;
	}

	function nextCard(isKnown: boolean) {
		if (isKnown) {
			cards[currentCardIndex].status = CardStatus.LEARN;
			CardService.update(cards[currentCardIndex]).catch(console.error);
		} else {
			cards[currentCardIndex].status = CardStatus.KNOWN;
			CardService.update(cards[currentCardIndex]).catch(console.error);
		}

		let nextIndex = currentCardIndex+1;

		if (nextIndex < cards.length) {
			setShowAnswer(false);
			setCurrentCardIndex(currentCardIndex+1);
		} else {
			router.back();
		}
	}

	return (
		<View style={{ flex: 1, backgroundColor: Colors.secondary, paddingTop: insets.top, paddingBottom: insets.bottom }}>
			<View style={{
				flexDirection: 'row',
				justifyContent: 'space-between',
				alignItems: 'center',
				paddingHorizontal: 24,
				paddingBottom: 24,
				marginBottom: 24
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

			<View style={{flexDirection: 'column', flex: 1}}>
				<View style={{
					flex: 1,
					alignItems: 'center',
					justifyContent: 'center',
					borderWidth: 4,
					borderColor: Colors.primary,
					borderRadius: 24,
					marginHorizontal: 36
				}}>
					<Text style={{
						fontSize: FONT_SIZES.heading,
						alignSelf: 'center',
						color: Colors.primary
					}}>{getCardContent()}</Text>
				</View>
				<View style={{
					flexDirection: 'row',
					justifyContent: 'space-between',
					alignItems: 'center',
					marginTop: 24,
					paddingVertical: 12,
					paddingHorizontal: 36
				}}>
					<Pressable style={{
						width: 64,
						height: 56,
						alignItems: 'center',
						justifyContent: 'center',
						borderColor: Colors.primary,
						borderWidth: 4,
						borderRadius: 12,
						backgroundColor: Colors.secondary,
					}} onPress={() => nextCard(false)}>
						<Entypo name="cross" size={FONT_SIZES.heading2} color={Colors.primary} /></Pressable>
					<Pressable style={{
						flex: 1,
						marginHorizontal: 12,
						alignItems: 'center',
						justifyContent: 'center',
						backgroundColor: Colors.primary,
						borderRadius: 12,
						padding: 11,
					}} onPress={() => setShowAnswer(!showAnswer)}
					><Text style={{color: Colors.secondary, fontSize: FONT_SIZES.heading2}}>Answer</Text></Pressable>
					<Pressable style={{
						width: 64,
						height: 56,
						alignItems: 'center',
						justifyContent: 'center',
						borderColor: Colors.primary,
						borderWidth: 4,
						borderRadius: 12,
						backgroundColor: Colors.secondary,
					}} onPress={() => nextCard(true)}>
						<Entypo name="check" size={FONT_SIZES.heading2} color={Colors.primary} /></Pressable>
				</View>
			</View>
		</View>
	);
}

