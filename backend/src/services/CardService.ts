import { Service } from 'typedi';
import { Card, CardStatus } from '../models/Card';
import { CardRepository } from '../repository/CardRepository';

@Service()
export class CardService {
	constructor(private cardRepository: CardRepository) {}

	async getAllCards(setId?: string) {
		return await this.cardRepository.getAllCards(setId);
	}

	async getCardById(cardId: string) {
		return await this.cardRepository.getCardById(cardId);
	}

	async createCard(setId: string, question: string, answer: string, status: CardStatus) {
		if (!setId) {
			throw new Error('setId is required');
		}

		if (!question) {
			throw new Error('question is required');
		}

		if (!answer) {
			throw new Error('answer is required');
		}

		if (status && !Object.values(CardStatus).includes(status)) {
			throw new Error('Invalid status');
		}

		const card = new Card(setId, question, answer, status);
		return await this.cardRepository.createCard(card);
	}

	async updateCard(cardId: string, question: string, answer: string, status: CardStatus) {
		if (status && !Object.values(CardStatus).includes(status)) {
			throw new Error('Invalid status');
		}

		const card: Partial<Card> = { ... (question ? { question } : {}), ... (answer ? { answer } : {}), ... (status ? { status } : {}) };
		return await this.cardRepository.updateCard(cardId, card);
	}

	async deleteCard(cardId: string) {
		return await this.cardRepository.deleteCard(cardId);
	}
}

