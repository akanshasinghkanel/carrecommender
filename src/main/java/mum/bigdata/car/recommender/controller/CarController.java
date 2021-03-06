package mum.bigdata.car.recommender.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import mum.bigdata.car.recommender.model.Car;
import mum.bigdata.car.recommender.model.Make;
import mum.bigdata.car.recommender.model.Model;
import mum.bigdata.car.recommender.model.User;
import mum.bigdata.car.recommender.service.CarService;
import mum.bigdata.car.recommender.service.MakeService;
import mum.bigdata.car.recommender.service.ModelService;
import mum.bigdata.car.recommender.util.Recommender;

@Controller
public class CarController {

	@Autowired
	private MakeService makeService;

	@Autowired
	private ModelService modelService;

	@Autowired
	private CarService carService;

	@RequestMapping(value = "car", method = RequestMethod.GET)
	public ModelAndView getCar(@RequestParam("cid") long cid) {
		ModelAndView modelAndView = new ModelAndView("cardetails");
		modelAndView.addObject("car", carService.getCar(cid));

		return modelAndView;
	}

	@RequestMapping(value = "makes")
	public @ResponseBody List<Make> getMakes(HttpServletRequest request) {
		return makeService.getMakes();
	}

	@RequestMapping(value = "models")
	public @ResponseBody List<Model> getModels(@RequestParam("make") String makeName) {
		return modelService.getModels(makeName);
	}

	@RequestMapping(value = "years")
	public @ResponseBody List<Model> getYears(HttpServletRequest request) {

		List<Model> models = new ArrayList<>();
		models.add(new Model(2, "2016"));
		models.add(new Model(1, "2015"));
		models.add(new Model(1, "2014"));

		return models;
	}

	@RequestMapping(value = "searchCar")
	public @ResponseBody List<Car> getCars(@RequestParam("make") String makeName,
			@RequestParam("model") String modelName, @RequestParam("year") String year) {
		return carService.getCars(makeName, modelName, year);
	}

	@RequestMapping(value = "tracker")
	public @ResponseBody String trackSearch(@RequestParam("carid") String specimen, HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		String id = "";
		if (user != null && user.getId() != null) {
			id = user.getId();
			Recommender rec = new Recommender(id);
			rec.trackSearch(specimen);
		}

		return null;
	}

	@RequestMapping(value = "recommendations")
	public @ResponseBody List<Car> getRecommendations(HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		// If not logged in, no recommendations
		if (user == null || user.getId() == null) {
			return null;
		}

		Recommender rec = new Recommender(user.getId());
		ArrayList<Car> recommendations = rec.getRecommendation();

		return recommendations;
	}
}
