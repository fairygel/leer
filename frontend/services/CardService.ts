import { fetcher } from './fetcher';
import { FlashCard } from '@/types';

export const CardService = {
	async getAll(setId: string): Promise<FlashCard[]> {
		const response = await fetcher(`/sets/${setId}/flashcards`, 'GET');
		if (!response.ok) {
			throw new Error('Failed to fetch cards');
		}
		return await response.json();
	},

	async getOne(setId: string, cardId: string): Promise<FlashCard> {
		const response = await fetcher(`/sets/${setId}/flashcards/${cardId}`, 'GET');
		if (!response.ok) {
			throw new Error('Failed to fetch card');
		}
		return await response.json();
	},

	async create(setId: string, data: Partial<FlashCard>): Promise<FlashCard> {
		const response = await fetcher(`/sets/${setId}/flashcards`, 'POST', {
			body: JSON.stringify(data),
		});
		if (!response.ok) {
			throw new Error('Failed to create card');
		}
		return await response.json();
	},

	async update(card: Partial<FlashCard>): Promise<any> {
		const response = await fetcher(`/sets/${card.setId}/flashcards/${card._id}`, 'PATCH', {
			body: JSON.stringify(card),
		});
		if (!response.ok) {
			throw new Error('Failed to update card');
		}
		return await response.json();
	},

	async delete(setId: string, cardId: string): Promise<void> {
		const response = await fetcher(`/sets/${setId}/flashcards/${cardId}`, 'DELETE');
		if (!response.ok) {
			throw new Error('Failed to delete card');
		}
	},
};

