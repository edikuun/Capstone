package Emitters;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.particles.Particle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import Emitters.OneTimeEmitter.ColorRecord;

public class OneTimeEmitterIO {
	public static OneTimeEmitter loadEmitter(InputStream stream){
		DocumentBuilder builder;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = builder.parse(stream);
			if (!document.getDocumentElement().getNodeName().equals("emitter")) {
				throw new IOException("Not a particle emitter file");
			}
			OneTimeEmitter emitter = new OneTimeEmitter("new");
			elementToEmitter(document.getDocumentElement(), emitter);
			return emitter;
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private static void elementToEmitter(Element element,
			OneTimeEmitter emitter) {
		emitter.name = element.getAttribute("name");
		emitter.setImageName(element.getAttribute("imageName"));

		String renderType = element.getAttribute("renderType");
		emitter.usePoints = Particle.INHERIT_POINTS;
		if (renderType.equals("quads")) {
			emitter.usePoints = Particle.USE_QUADS;
		}
		if (renderType.equals("points")) {
			emitter.usePoints = Particle.USE_POINTS;
		}

		String useOriented = element.getAttribute("useOriented");
		if (useOriented != null)
			emitter.useOriented = "true".equals(useOriented);

		String useAdditive = element.getAttribute("useAdditive");
		if (useAdditive != null)
			emitter.useAdditive = "true".equals(useAdditive);

		parseRangeElement(getFirstNamedElement(element, "spawnInterval"),
				emitter.spawnInterval);
		parseRangeElement(getFirstNamedElement(element, "spawnCount"),
				emitter.spawnCount);
		parseRangeElement(getFirstNamedElement(element, "initialLife"),
				emitter.initialLife);
		parseRangeElement(getFirstNamedElement(element, "initialSize"),
				emitter.initialSize);
		parseRangeElement(getFirstNamedElement(element, "xOffset"),
				emitter.xOffset);
		parseRangeElement(getFirstNamedElement(element, "yOffset"),
				emitter.yOffset);
		parseRangeElement(getFirstNamedElement(element, "initialDistance"),
				emitter.initialDistance);
		parseRangeElement(getFirstNamedElement(element, "speed"), emitter.speed);
		parseRangeElement(getFirstNamedElement(element, "length"),
				emitter.length);
		parseRangeElement(getFirstNamedElement(element, "emitCount"),
				emitter.emitCount);

		parseValueElement(getFirstNamedElement(element, "spread"),
				emitter.spread);
		parseValueElement(getFirstNamedElement(element, "angularOffset"),
				emitter.angularOffset);
		parseValueElement(getFirstNamedElement(element, "growthFactor"),
				emitter.growthFactor);
		parseValueElement(getFirstNamedElement(element, "gravityFactor"),
				emitter.gravityFactor);
		parseValueElement(getFirstNamedElement(element, "windFactor"),
				emitter.windFactor);
		parseValueElement(getFirstNamedElement(element, "startAlpha"),
				emitter.startAlpha);
		parseValueElement(getFirstNamedElement(element, "endAlpha"),
				emitter.endAlpha);
		parseValueElement(getFirstNamedElement(element, "alpha"), emitter.alpha);
		parseValueElement(getFirstNamedElement(element, "size"), emitter.size);
		parseValueElement(getFirstNamedElement(element, "velocity"),
				emitter.velocity);
		parseValueElement(getFirstNamedElement(element, "scaleY"),
				emitter.scaleY);

		Element color = getFirstNamedElement(element, "color");
		NodeList steps = color.getElementsByTagName("step");
		emitter.colors.clear();
		for (int i = 0; i < steps.getLength(); i++) {
			Element step = (Element) steps.item(i);
			float offset = Float.parseFloat(step.getAttribute("offset"));
			float r = Float.parseFloat(step.getAttribute("r"));
			float g = Float.parseFloat(step.getAttribute("g"));
			float b = Float.parseFloat(step.getAttribute("b"));

			emitter.addColorPoint(offset, new Color(r, g, b, 1));
		}

		// generate new random play length
		emitter.replay();
	}
	
	private static Element getFirstNamedElement(Element element, String name) {
		NodeList list = element.getElementsByTagName(name);
		if (list.getLength() == 0) {
			return null;
		}

		return (Element) list.item(0);
	}
	
	private static void parseRangeElement(Element element,
			OneTimeEmitter.Range range) {
		if (element == null) {
			return;
		}
		range.setMin(Float.parseFloat(element.getAttribute("min")));
		range.setMax(Float.parseFloat(element.getAttribute("max")));
		range.setEnabled("true".equals(element.getAttribute("enabled")));
	}
	
	private static void parseValueElement(Element element,
			OneTimeEmitter.Value value) {
		if (element == null) {
			return;
		}

		String type = element.getAttribute("type");
		String v = element.getAttribute("value");

		if (type == null || type.length() == 0) {
			// support for old style which did not write the type
			if (value instanceof OneTimeEmitter.SimpleValue) {
				((OneTimeEmitter.SimpleValue) value).setValue(Float.parseFloat(v));
			} else if (value instanceof OneTimeEmitter.RandomValue) {
				((OneTimeEmitter.RandomValue) value).setValue(Float.parseFloat(v));
			} else {
				System.out.println("problems reading element, skipping: " + element);
			}
		} else {
			// type given: this is the new style
			if (type.equals("simple")) {
				((OneTimeEmitter.SimpleValue) value).setValue(Float.parseFloat(v));
			} else if (type.equals("random")) {
				((OneTimeEmitter.RandomValue) value).setValue(Float.parseFloat(v));
			} else if (type.equals("linear")) {
				String min = element.getAttribute("min");
				String max = element.getAttribute("max");
				String active = element.getAttribute("active");

				NodeList points = element.getElementsByTagName("point");

				ArrayList curve = new ArrayList();
				for (int i = 0; i < points.getLength(); i++) {
					Element point = (Element) points.item(i);

					float x = Float.parseFloat(point.getAttribute("x"));
					float y = Float.parseFloat(point.getAttribute("y"));

					curve.add(new Vector2f(x, y));
				}

				((OneTimeEmitter.LinearInterpolator) value).setCurve(curve);
				((OneTimeEmitter.LinearInterpolator) value).setMin(Integer.parseInt(min));
				((OneTimeEmitter.LinearInterpolator) value).setMax(Integer.parseInt(max));
				((OneTimeEmitter.LinearInterpolator) value).setActive("true".equals(active));
			} else {
				System.out.println("unkown type detected: " + type);
			}
		}
	}
	
	public static void saveEmitter(OutputStream out, OneTimeEmitter emitter){
		try{
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document document = builder.newDocument();
	
			document.appendChild(emitterToElement(document, emitter));
			Result result = new StreamResult(new OutputStreamWriter(out,
					"utf-8"));
			DOMSource source = new DOMSource(document);
	
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer xformer = factory.newTransformer();
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
	
			xformer.transform(source, result);
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private static Element emitterToElement(Document document,
			OneTimeEmitter emitter) {
		Element root = document.createElement("emitter");
		root.setAttribute("name", emitter.name);
		root.setAttribute("imageName", emitter.imageName == null ? ""
				: emitter.imageName);
		root
				.setAttribute("useOriented", emitter.useOriented ? "true"
						: "false");
		root
				.setAttribute("useAdditive", emitter.useAdditive ? "true"
						: "false");

		if (emitter.usePoints == Particle.INHERIT_POINTS) {
			root.setAttribute("renderType", "inherit");
		}
		if (emitter.usePoints == Particle.USE_POINTS) {
			root.setAttribute("renderType", "points");
		}
		if (emitter.usePoints == Particle.USE_QUADS) {
			root.setAttribute("renderType", "quads");
		}

		root.appendChild(createRangeElement(document, "spawnInterval",
				emitter.spawnInterval));
		root.appendChild(createRangeElement(document, "spawnCount",
				emitter.spawnCount));
		root.appendChild(createRangeElement(document, "initialLife",
				emitter.initialLife));
		root.appendChild(createRangeElement(document, "initialSize",
				emitter.initialSize));
		root.appendChild(createRangeElement(document, "xOffset",
				emitter.xOffset));
		root.appendChild(createRangeElement(document, "yOffset",
				emitter.yOffset));
		root.appendChild(createRangeElement(document, "initialDistance",
				emitter.initialDistance));
		root.appendChild(createRangeElement(document, "speed", emitter.speed));
		root
				.appendChild(createRangeElement(document, "length",
						emitter.length));
		root.appendChild(createRangeElement(document, "emitCount",
				emitter.emitCount));

		root
				.appendChild(createValueElement(document, "spread",
						emitter.spread));
		root.appendChild(createValueElement(document, "angularOffset",
				emitter.angularOffset));
		root.appendChild(createValueElement(document, "growthFactor",
				emitter.growthFactor));
		root.appendChild(createValueElement(document, "gravityFactor",
				emitter.gravityFactor));
		root.appendChild(createValueElement(document, "windFactor",
				emitter.windFactor));
		root.appendChild(createValueElement(document, "startAlpha",
				emitter.startAlpha));
		root.appendChild(createValueElement(document, "endAlpha",
				emitter.endAlpha));
		root.appendChild(createValueElement(document, "alpha", emitter.alpha));
		root.appendChild(createValueElement(document, "size", emitter.size));
		root.appendChild(createValueElement(document, "velocity",
				emitter.velocity));
		root
				.appendChild(createValueElement(document, "scaleY",
						emitter.scaleY));

		Element color = document.createElement("color");
		ArrayList list = emitter.colors;
		for (int i = 0; i < list.size(); i++) {
			ColorRecord record = (ColorRecord) list.get(i);
			Element step = document.createElement("step");
			step.setAttribute("offset", "" + record.pos);
			step.setAttribute("r", "" + record.col.r);
			step.setAttribute("g", "" + record.col.g);
			step.setAttribute("b", "" + record.col.b);

			color.appendChild(step);
		}

		root.appendChild(color);

		return root;
	}
	
	private static Element createRangeElement(Document document, String name,
			OneTimeEmitter.Range range) {
		Element element = document.createElement(name);
		element.setAttribute("min", "" + range.getMin());
		element.setAttribute("max", "" + range.getMax());
		element.setAttribute("enabled", "" + range.isEnabled());

		return element;
	}
	
	private static Element createValueElement(Document document, String name,
			OneTimeEmitter.Value value) {
		Element element = document.createElement(name);

		// void: now writes the value type
		if (value instanceof OneTimeEmitter.SimpleValue) {
			element.setAttribute("type", "simple");
			element.setAttribute("value", "" + value.getValue(0));
		} else if (value instanceof OneTimeEmitter.RandomValue) {
			element.setAttribute("type", "random");
			element
					.setAttribute("value", ""
							+ ((OneTimeEmitter.RandomValue) value).getValue());
		} else if (value instanceof OneTimeEmitter.LinearInterpolator) {
			element.setAttribute("type", "linear");
			element.setAttribute("min", ""
					+ ((OneTimeEmitter.LinearInterpolator) value).getMin());
			element.setAttribute("max", ""
					+ ((OneTimeEmitter.LinearInterpolator) value).getMax());
			element.setAttribute("active", ""
					+ ((OneTimeEmitter.LinearInterpolator) value).isActive());

			ArrayList curve = ((OneTimeEmitter.LinearInterpolator) value).getCurve();
			for (int i = 0; i < curve.size(); i++) {
				Vector2f point = (Vector2f) curve.get(i);

				Element pointElement = document.createElement("point");
				pointElement.setAttribute("x", "" + point.x);
				pointElement.setAttribute("y", "" + point.y);

				element.appendChild(pointElement);
			}
		} else {
			
		}

		return element;
	}
}
