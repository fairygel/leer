import { Request, Response } from 'express';
import { Card, CardStatus } from '../models/Card';
import { CardRepository } from '../repository/cardRepository';
import { Service } from 'typedi';

@Service()
export class CardController {
	private repo: CardRepository;

	constructor(repo: CardRepository) {
		this.repo = repo;
	}

	async indexCard(req: Request, res: Response) {
		const { setId } = req.query;
		const cards = await this.repo.getAllCards(setId ? String(setId) : undefined);

		return res.json(cards);
	}

	async readCard(req: Request, res: Response) {
		const { cardId } = req.params;
		const card = await this.repo.getCardById(cardId);

		if (!card) {
			return res.status(404).json({ error: 'Card not found' });
		}

		return res.json(card);
	}

	async createCard(req: Request, res: Response) {
		const { setId, question, answer, status } = req.body;

		if (!setId) {
			return res.status(400).json({ error: 'setId is required' });
		}

		if (!question) {
			return res.status(400).json({ error: 'question is required' });
		}

		if (!answer) {
			return res.status(400).json({ error: 'answer is required' });
		}

		if (status && !Object.values(CardStatus).includes(status)) {
			return res.status(400).json({ error: 'Invalid status' });
		}

		const card = new Card(setId, question, answer, status ?? CardStatus.NEW);

		return res.status(201).json(await this.repo.createCard(card));
	}

	async updateCard(req: Request, res: Response) {
		const { cardId } = req.params;
		const { question, answer, status } = req.body;

		if (status && !Object.values(CardStatus).includes(status)) {
			return res.status(400).json({ error: 'Invalid status' });
		}

		let card = new Card(question, answer, status);

		const updated = await this.repo.updateCard(cardId, card);

		if (!updated.matchedCount) {
			return res.status(404).json({ error: 'Card not found' });
		}

		return res.json(card);
	}

	async deleteCard(req: Request, res: Response) {
		const { id } = req.params;

		const result = await this.repo.deleteCard(id);

		if (!result.deletedCount) {
			return res.status(404).json({ error: 'Card not found' });
		}
		return res.status(204).send();
	}
}
