package org.tltv.gantt;

import com.vaadin.shared.Connector;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import org.tltv.gantt.client.shared.Step;
import org.tltv.gantt.client.shared.StepState;
import org.tltv.gantt.client.shared.SubStep;
import org.tltv.gantt.client.shared.SubStepObserver;
import org.tltv.gantt.client.shared.SubStepObserverProxy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Component representing a Step in the Gantt chart.
 *
 * @author Tltv
 *
 */
public class StepComponent extends AbstractStepComponent implements
        HasComponents, SubStepObserver {

    protected Gantt gantt;

    private SubStepComponentFactory subStepComponentFactory = new SubStepComponentFactory();

    public StepComponent(Gantt gantt, Step data) {
        this.gantt = gantt;
        if (data.getUid() == null) {
            data.setUid(UUID.randomUUID().toString());
        }
        setParent(gantt);
        getState().step = data;
        for (SubStep subStep : data.getSubSteps()) {
            onAddSubStep(subStep);
        }
        data.addSubStepObserver(new SubStepObserverProxy(this));
    }

    @Override
    public StepState getState() {
        return (StepState) super.getState();
    }

    @Override
    public StepState getState(boolean markAsDirty) {
        return (StepState) super.getState(markAsDirty);
    }

    public void addSubStep(StepComponent stepComponent, SubStep subStep) {
        SubStepComponent component = createSubStepComponent(stepComponent,
                subStep);
        getState().subSteps.add(component);

    }

    protected SubStepComponent createSubStepComponent(
            StepComponent stepComponent, SubStep subStep) {
        return this.subStepComponentFactory.create(stepComponent, subStep);
    }

    @Override
    public Iterator<Component> iterator() {
        List<Component> l = new ArrayList<Component>();
        for (Connector c : getState(false).subSteps) {
            l.add((Component) c);
        }
        return l.iterator();
    }

    @Override
    public void onAddSubStep(SubStep subStep) {
        SubStepComponent component = createSubStepComponent(this, subStep);
        getState(true).subSteps.add(component);
        gantt.subStepMap.put(subStep.getUid(), component);
        gantt.adjustDatesByAbstractStep(subStep.getOwner());
    }

    /** Detach sub-step component from the UI. */
    @Override
    public void onRemoveSubStep(SubStep subStep) {
        SubStepComponent component = gantt.subStepMap.get(subStep.getUid());
        if (component != null) {
            component.setParent(null);
            getState(true).subSteps.remove(component);
        }
        gantt.subStepMap.remove(subStep.getUid());
        gantt.adjustDatesByAbstractStep(subStep.getOwner());
    }

    public void setSubStepComponentFactory(SubStepComponentFactory subStepComponentFactory) {
        this.subStepComponentFactory = subStepComponentFactory;
    }
}
