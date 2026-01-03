import { Request, Response } from 'express';
import { CardSet } from '../models/CardSet';
import { SetRepository } from '../repository/setRepository';
import { Service } from 'typedi';

@Service()
export class SetController {
	private repo: SetRepository;

	constructor(repo: SetRepository) {
		this.repo = repo;
	}

	async indexSet(req: Request, res: Response) {
		return res.json(await this.repo.getAllSets());
	}

	async readSet(req: Request, res: Response) {
		const { setId } = req.params;
		const set = await this.repo.getSetById(setId);

		if (!set) {
			return res.status(404).json({ error: 'Set not found' });
		}

		return res.json(set);
	}

	async createSet(req: Request, res: Response) {
		const { name, description } = req.body;
		if (!name) {
			return res.status(400).json({ error: 'name is required' });
		}

		const set = new CardSet(name, description);

		return res.status(201).json(await this.repo.createSet(set));
	}

	async updateSet(req: Request, res: Response) {
		const { setId } = req.params;
		const { name, description } = req.body;

		let set = new CardSet(name, description);

		const updated = await this.repo.updateSet(setId, set);

		if (!updated.matchedCount) {
			return res.status(404).json({ error: 'Set not found' });
		}

		return res.json(updated);
	}

	async deleteSet(req: Request, res: Response) {
		const { setId } = req.params;

		const result = await this.repo.deleteSet(setId);

		if (!result.deletedCount) {
			return res.status(404).json({ error: 'Set not found' });
		}
		return res.status(204).send();
	}
}
