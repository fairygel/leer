import { Request, Response } from 'express';
import { CardStatus } from '../models/Card';
import { CardService } from '../services/CardService';
import { Service } from 'typedi';

@Service()
export class CardController {
	constructor(private cardService: CardService) {}

	async indexCard(req: Request, res: Response) {
		try {
			const { setId } = req.query;

			const cards = await this.cardService.getAllCards(setId ? String(setId) : undefined);
			return res.json(cards);
		} catch (error: any) {
			return res.status(500).json({ error: error.message });
		}
	}

	async readCard(req: Request, res: Response) {
		try {
			const { cardId } = req.params;
			const card = await this.cardService.getCardById(cardId);

			if (!card) {
				return res.status(404).json({ error: 'Card not found' });
			}

			return res.json(card);
		} catch (error: any) {
			return res.status(500).json({ error: error.message });
		}
	}

	async createCard(req: Request, res: Response) {
		try {
			const { setId } = req.params
			const { question, answer, status } = req.body;

			const result = await this.cardService.createCard(setId, question, answer, status ?? CardStatus.NEW);
			return res.status(201).json(result);
		} catch (error: any) {
			return res.status(400).json({ error: error.message });
		}
	}

	async updateCard(req: Request, res: Response) {
		try {
			const { cardId } = req.params;
			const { question, answer, status } = req.body;

			const updated = await this.cardService.updateCard(cardId, question, answer, status);

			if (!updated.matchedCount) {
				return res.status(404).json({ error: 'Card not found' });
			}

			return res.json(updated);
		} catch (error: any) {
			return res.status(400).json({ error: error.message });
		}
	}

	async deleteCard(req: Request, res: Response) {
		try {
			const { cardId } = req.params;

			const result = await this.cardService.deleteCard(cardId);

			if (!result.deletedCount) {
				return res.status(404).json({ error: 'Card not found' });
			}
			return res.status(204).send();
		} catch (error: any) {
			return res.status(500).json({ error: error.message });
		}
	}
}
