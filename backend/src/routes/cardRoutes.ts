import express from 'express';
import { CardController } from '../controllers/cardController';
import { Service } from 'typedi';

@Service()
export class CardRouter {
	constructor(private cardController: CardController) {}

	createCardRoutes() {
		const router = express.Router({ mergeParams: true });

		router.get(
			'/',
			this.cardController.indexCard.bind(this.cardController)
		);
		router.get(
			'/:cardId',
			this.cardController.readCard.bind(this.cardController)
		);
		router.post(
			'/',
			this.cardController.createCard.bind(this.cardController)
		);
		router.patch(
			'/:cardId',
			this.cardController.updateCard.bind(this.cardController)
		);
		router.delete(
			'/:cardId',
			this.cardController.deleteCard.bind(this.cardController)
		);

		return router;
	}
}
