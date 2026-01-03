import express from 'express';
import { Service } from 'typedi';
import { SetController } from '../controllers/setController';
import { CardRouter } from './cardRoutes';

@Service()
export class SetRouter {
	constructor(
        private setController: SetController,
        private cardRouter: CardRouter
    ) {}

	createSetRoutes() {
		const router = express.Router();

		router.get('/', this.setController.indexSet.bind(this.setController));
		router.get(
			'/:setId',
			this.setController.readSet.bind(this.setController)
		);
		router.post('/', this.setController.createSet.bind(this.setController));
		router.patch(
			'/:setId',
			this.setController.updateSet.bind(this.setController)
		);
		router.delete(
			'/:setId',
			this.setController.deleteSet.bind(this.setController)
		);
		router.use('/:setId/flashcards', this.cardRouter.createCardRoutes());

		return router;
	}
}
